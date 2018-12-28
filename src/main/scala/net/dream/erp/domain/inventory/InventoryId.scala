package net.dream.erp.domain.inventory

case class InventoryId(partId: Long){
  require(partId > 0)
}
