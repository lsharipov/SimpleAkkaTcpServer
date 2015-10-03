package com.lenar

import akka.actor.ActorSystem

object ApplicationMain extends App {
  val host = "localhost"
  val port = 8089

  val system = ActorSystem("MyActorSystem")
  val frontend = system.actorOf(TcpFrontend.props(host, port), "FrontendActor")

  system.awaitTermination()
}