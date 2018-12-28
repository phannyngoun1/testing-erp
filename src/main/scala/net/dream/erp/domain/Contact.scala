package net.dream.erp.domain

import net.dream.erp.domain.Common.ObjectType
import net.dream.erp.domain.Contact.ContactType

object Contact {
  trait ContactType extends ObjectType

  abstract class AbstractContactType(id: Int, name: String) extends ContactType {
    override def getId: Int = id

    override def getName: String = name
  }


  case class Other(id: Int, name: String) extends AbstractContactType(id, name)



  case object Phone extends AbstractContactType(1, "Phone")

  case object Email extends AbstractContactType(2, "Email")

  case object Facebook extends AbstractContactType(3, "Facebook")
}


case class Contact(contactType: ContactType, value: String)
