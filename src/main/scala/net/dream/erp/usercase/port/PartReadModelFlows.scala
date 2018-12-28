package net.dream.erp.usercase.port

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.ExecutionContext

trait PartReadModelFlows {

  def resolveLastSeqNrSource(implicit ec: ExecutionContext): Source[Long, NotUsed]

  def addNewPartFlow: Flow[(Long, Long), Int, NotUsed]

  def updatePartFlow: Flow[(Long, Long), Int, NotUsed]

}
