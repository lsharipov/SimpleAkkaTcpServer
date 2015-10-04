package com.lenar

import java.net.InetSocketAddress

import akka.actor._
import akka.io._
import com.lenar.codecs.Codecs.FrameCodec

object TcpFrontend {
  def props(host: String, port: Int, createHandler: ActorRef => (Props, FrameCodec)): Props =
    Props(classOf[TcpFrontend], host, port, createHandler)
}

class TcpFrontend(host: String, port: Int, createHandler: ActorRef => (Props, FrameCodec))
  extends Actor with ActorLogging {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress(host, port))

  def receive = {
    case Bound(localAddress) =>
      log.info(s"Server bound to $localAddress")

    case CommandFailed(_: Bind) =>
      log.error(s"Binding failed")
      context stop self

    case Connected(remote, local) =>
      val connection = sender()

      val handler = context.actorOf(Props(classOf[SimplisticHandler], connection, createHandler))
      connection ! Register(handler)
      log.info(s"Client connected $remote -> $local")
  }
}
