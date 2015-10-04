package com.lenar.codecs

import akka.util.ByteString

object IdentityCodec {
  def read(buffer: ByteString): (ByteString, Seq[String]) = (ByteString.empty, Seq(buffer.utf8String))
  def write(data: String): ByteString = ByteString(data)
}
