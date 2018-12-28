package net.dream.erp.domain.part

object PartEvent {


  sealed trait PartEvent {

    val id: Long
  }

  case class NewPartEvent(
    val id: Long,
    nr: String,
    description: String,
    partTypeId: Int,
    uomId: Int
  ) extends PartEvent

  case class UpdatePartEvent(
    val id: Long,
    nr: String,
    description: String,
    partTypeId: Int,
    uomId: Int
  ) extends PartEvent

  case class DisablePartEvent(id: Long) extends PartEvent
}

