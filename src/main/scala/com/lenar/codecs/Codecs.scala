package com.lenar.codecs

import akka.util.ByteString

object Codecs {
  type FrameReader = ByteString => (ByteString, Seq[String])
  type FrameWriter = String => ByteString
  type FrameCodec = (FrameReader, FrameWriter)
}
