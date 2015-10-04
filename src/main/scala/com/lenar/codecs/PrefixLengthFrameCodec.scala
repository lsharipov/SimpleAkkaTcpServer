package com.lenar.codecs

import java.nio.{ByteBuffer, ByteOrder}
import akka.util.ByteString

import scala.collection.immutable.Queue

object PrefixLengthFrameCodec {
  val frameHeaderSize = 4

  def read(buffer: ByteString): (ByteString, Seq[String]) =
    read(buffer, Queue.empty)

  def read(buffer: ByteString, frames: Queue[String]): (ByteString, Queue[String]) =
    if (buffer.size < frameHeaderSize) {
      (buffer, frames)
    }
    else {
      val (lengthBytes, restBuffer) = buffer.splitAt(frameHeaderSize)
      val frameLength: Int = lengthBytes.toByteBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt

      if (restBuffer.size < frameLength)
        (buffer, frames)
      else {
        val (frame, nextBuffer) = restBuffer.splitAt(frameLength)
        read(nextBuffer, frames.enqueue(frame.utf8String))
      }
    }

  def write(data: String): ByteString = {
    val header = ByteBuffer.allocate(frameHeaderSize)
    header.putInt(data.length)
    ByteString(header) ++ ByteString(data)
  }
}


