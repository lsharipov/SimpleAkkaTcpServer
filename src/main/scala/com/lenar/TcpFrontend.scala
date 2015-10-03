package com.lenar

import java.net.InetSocketAddress
import java.nio.ByteOrder

import akka.actor.{ActorLogging, Props, Actor}
import akka.io._

/**
 * Created by lenar on 03.10.15.
 */

object TcpFrontend
{
  def props(host: String, port: Int): Props = Props(classOf[TcpFrontend], host, port)
}

class TcpFrontend(host: String, port: Int) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress(host, port))

  def receive = {
    case b @ Bound(localAddress) =>
    // do some logging or setup ...

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)
  }
}