package net.dream.erp.domain.inventory

import enumeratum.values._
import net.dream.erp.domain.accounting.Account
import net.dream.erp.domain.inventory.Product.{SoItemType, SubstituteProduct}
import org.sisioh.baseunits.scala.money.Money

object Product {

  sealed abstract class SoItemType(val value: Int, val name: String) extends IntEnumEntry

  case object SoItemType extends IntEnum[SoItemType] {

    case object Sale extends SoItemType(1, "Sale")

    case object DropShip extends SoItemType(2, "Drop ship")

    case object ReturnCredit extends SoItemType(3, "Return credit")

    val values = findValues
  }

  case class SubstituteProduct(product: Product, note: String)

}

case class Product(
  id: Long,
  nr: String,
  description: String,
  uoM: Option[UoM] = None,
  price: Option[Money],
  soldInDiffUoM: Boolean = true,
  taxable: Boolean = true,
  details: Option[String] = None,
  upc: Option[String] = None,
  sku: Option[String] = None,
  soItemType: Option[SoItemType] = None,
  substituteProd: Option[List[SubstituteProduct]] = None,
  incomeAccount: Option[Account] = None

)