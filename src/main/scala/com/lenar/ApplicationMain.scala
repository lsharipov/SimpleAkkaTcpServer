package com.lenar

import akka.actor._
import com.lenar.codecs.Codecs.FrameCodec
import com.lenar.codecs._

object ApplicationMain extends App {
  val host = "localhost"
  val port = 8089

  val system = ActorSystem("MyActorSystem")
  //val codec: FrameCodec = (PrefixLengthFrameCodec.read, PrefixLengthFrameCodec.write)
  val codec: FrameCodec = (IdentityCodec.read, IdentityCodec.write)
  val frontend = system.actorOf(TcpFrontend.props(host, port, out => (Props(classOf[ConnectionActor], out), codec)), "FrontendActor")

  system.awaitTermination()
}

class ConnectionActor(out: ActorRef) extends Actor with ActorLogging {

  override def receive: Actor.Receive = {

    case msg: String =>
      log.info(s"I received $msg. Sending reply...")
      out ! s"Received $msg"
  }

}

