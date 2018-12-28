package net.dream.erp.interfaces.service.inventory

import scala.concurrent.duration.Duration

case class InventoryServiceConfig(receiveTimeout: Duration, numOfEventsToSnapshot: Int)
