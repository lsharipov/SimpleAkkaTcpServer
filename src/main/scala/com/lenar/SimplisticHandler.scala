package com.lenar

import akka.actor.{Props, Actor, ActorRef, ActorLogging}
import akka.io.Tcp
import akka.util.ByteString
import codecs.Codecs.FrameCodec

class SimplisticHandler(connection: ActorRef, createHandler: ActorRef => (Props, FrameCodec))
  extends Actor with ActorLogging {

  import Tcp._

  val (wrapperProps, codec) = createHandler(self)
  val wrapper = context.actorOf(wrapperProps)
  val (read, write) = codec

  def receive: Receive = process(ByteString.empty)

  def process(buffer: ByteString): Receive = {

    case data: String =>
      connection ! Write(write(data))

    case Received(data) =>
      log.info(s"Received: $data")

      val (restBuffer, frames) = read(buffer ++ data)
      log.info(s"Parsed frames: $frames")

      frames foreach { wrapper ! _ }

      context become process(restBuffer)

    case PeerClosed =>
      log.info(s"Client disconnected: $sender")
      context stop self
  }
}
