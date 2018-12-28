package net.dream.erp.interfaces.service.part

import scala.concurrent.duration.Duration

case class PartServiceConfig (receiveTimeout: Duration, numOfEventsToSnapshot: Int)
