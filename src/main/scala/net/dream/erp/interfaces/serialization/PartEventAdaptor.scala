package net.dream.erp.interfaces.serialization

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import net.dream.erp.domain.part.PartEvent.PartEvent

class PartEventAdaptor extends WriteEventAdapter {

  private def withTag(event: Any, tag: String) = Tagged(event, Set(tag))

  private val tagName = classOf[PartEvent].getName

  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = {
    withTag(event, tagName)
  }
}
