package net.dream.erp.domain.inventory

import enumeratum.values._
import net.dream.erp.domain.inventory.UoM.Measurement

object UoM {

  sealed abstract class Measurement(val value: Int, val name: String) extends IntEnumEntry

  case object Measurement extends IntEnum[Measurement] {

    val values = findValues

    case object Count extends Measurement(1, "Count")

    case object Weight extends Measurement(2, "Weight")

    case object Length extends Measurement(3, "Length")

    case object Area extends Measurement(4, "Area")

    case object Volume extends Measurement(5, "Volume")

    case object Time extends Measurement(6, "Time")
  }

}


case class UoM(
  id: Int,
  abbr: String,
  name: String,
  description: String,
  measurement: Measurement,
  active: Boolean = true,
  readonly: Boolean = false
)
