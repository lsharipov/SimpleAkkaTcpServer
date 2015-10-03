package com.lenar

import java.nio.ByteOrder

import akka.actor.Actor
import akka.io.{IO, Tcp}
import akka.io.Tcp.{Received, Write, PeerClosed}
import akka.util.ByteString


/**
 * Created by lenar on 03.10.15.
 */
class SimplisticHandler extends Actor {
  import Tcp._

  def receive : Receive = process (ByteString.empty)

  def process (byteString: ByteString) : Receive = {
    case Received(data) => {
      val(notParsedBytes, frames) = parse (byteString ++ data, List.empty)
      context become process(notParsedBytes)

      frames foreach println
    }
    case PeerClosed => context stop self
  }

  def parse (byteString: ByteString, frames:List[String]) : (ByteString, List[String]) = {
    if (byteString.length >= 4) {
      val slice = byteString.slice(0, 4).asByteBuffer

      val length = slice.order(ByteOrder.LITTLE_ENDIAN).getInt

      if (byteString.length >= 4 + length) {
        val frame = byteString.slice(4, 4 + length).decodeString("UTF8")
        val newFrames = frames :+ frame
        val notParsedBytes = byteString.takeRight(byteString.length - (4 + length))
        parse (notParsedBytes, newFrames)
      } else {
        (byteString, frames)
      }
    } else {
      (byteString, frames)
    }
  }
}