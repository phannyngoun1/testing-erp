package net.dream.erp.domain.vendor

import net.dream.erp.domain.{Address, Contact}

object Vendor {

}

case class Vendor(id: Int, name: String, address: Address, contacts: List[Contact])