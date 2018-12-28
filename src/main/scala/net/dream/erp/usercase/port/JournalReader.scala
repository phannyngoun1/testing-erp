package net.dream.erp.usercase.port

import akka.NotUsed
import akka.stream.scaladsl.Source

trait JournalReader {
  def eventsByTagSource(tag: String, seqNr: Long): Source[EventBody, NotUsed]
}
