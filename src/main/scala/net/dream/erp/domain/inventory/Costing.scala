package net.dream.erp.domain.inventory

import enumeratum.values._
import net.dream.erp.domain.inventory.Costing._
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.time.TimePoint


object Costing {

  sealed abstract class CostingMethod(val value: Int, val name: String) extends IntEnumEntry

  case object CostingMethod extends IntEnum[CostingMethod] {

    val values = findValues

    case object Std extends CostingMethod(1, "Standard")

    case object Avg extends CostingMethod(2, "Average")

    case object FiFo extends CostingMethod(3, "FiFo")

    case object LiFo extends CostingMethod(4, "LiFo")

  }

}

abstract class Costing(unitCost: Money, totalCost: Money, createdAt: TimePoint, costingMethod: Option[CostingMethod] = None )

case class StdCosting(unitCost: Money, totalCost: Money, createdAt: TimePoint)
  extends Costing(unitCost, totalCost , createdAt, Some(CostingMethod.Std))

case class AvgCosting(unitCost: Money, totalCost: Money, createdAt: TimePoint)
  extends Costing(unitCost, totalCost, createdAt, Some(CostingMethod.Avg))

case class CostLayer(unitCost: Money, totalCost: Money, createdAt: TimePoint)
  extends Costing(unitCost, totalCost, createdAt)

