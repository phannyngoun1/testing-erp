package net.dream.erp.usercase

object UseCaseConfig {

  case class PartAggregateUseCaseConfig(bufferSize: Int)
  case class InventoryAggregateUseCaseConfig(bufferSize: Int)

}
