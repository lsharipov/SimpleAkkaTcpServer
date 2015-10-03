package com.lenar

import akka.actor.Actor
import akka.io.{IO, Tcp}
import akka.io.Tcp.{Received, Write, PeerClosed}
import akka.util.ByteString

/**
 * Created by lenar on 03.10.15.
 */
class SimplisticHandler extends Actor {
  import Tcp._

  def receive = {
    case Received(data) => {
      // sender() ! Write(data)
      println (data.decodeString("US-ASCII"))
    }
    case PeerClosed => context stop self
  }
}