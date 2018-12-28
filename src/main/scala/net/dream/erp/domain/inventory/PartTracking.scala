package net.dream.erp.domain.inventory

import enumeratum.values._
import net.dream.erp.domain.inventory.PartTracking.TrackingValue
import org.sisioh.baseunits.scala.time.TimePoint

object PartTracking {

  sealed abstract class TrackingType(val value: Int, val name: String) extends IntEnumEntry


  case object TrackingType extends IntEnum[TrackingType] {

    val values = findValues

    case object Text extends TrackingType(1, "Text")

    case object Date extends TrackingType(2, "Date")

    case object Number extends TrackingType(3, "Number")

    case object Serial extends TrackingType(4, "Serial")

    case object Lot extends TrackingType(5, "Lot Number")

    case object Expiry extends TrackingType(6, "Expiry date")

  }

  sealed abstract class TrackingValue(trackingType: TrackingType)

  case class TextTrackingValue(value: String) extends TrackingValue(TrackingType.Text)

  case class DateTrackingValue(value: TimePoint) extends TrackingValue(TrackingType.Date)

  case class NumberTrackingValue(value: Int) extends TrackingValue(TrackingType.Number)

  case class SerialTrackingValue(values: List[String]) extends TrackingValue(TrackingType.Serial)

}
