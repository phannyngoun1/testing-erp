package net.dream.erp.domain.uom

import org.sisioh.baseunits.scala.time.TimePoint
import org.sisioh.baseunits.scala.timeutil.Clock

object UoMEvent {

  sealed trait UoMEvent {
    val id: Int
    val occurredAt: TimePoint
  }

  case class GetUoMEvent(override val id: Int, override val occurredAt: TimePoint = Clock.now) extends UoMEvent

}
