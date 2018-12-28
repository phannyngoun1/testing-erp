package net.dream.erp.interfaces.service.setting

import net.dream.erp.interfaces.service.setting.IdGeneration.IdGenType

object IdsState {

  trait IdEvent {
    val nextId: Long
  }

  case class NextIdEvent(idGenType: IdGenType, nextId: Long) extends IdEvent

  case class NewIdEvent(idGenType: IdGenType, nextId: Long = 1) extends IdEvent

  def empty = IdsState(ids = Map.empty)

  def applyState(ids: Map[IdGenType, Long]) = IdsState(ids = ids)

}

case class IdsState private(
  private val ids: Map[IdGenType, Long]
) {

  import IdsState._

  def getIds = ids

  def nextIdEvent(idGenType: IdGenType): IdEvent = {
    if (ids.exists(p => p._1 == idGenType)) {
      NextIdEvent(idGenType, ids(idGenType) + 1)
    } else {
      NewIdEvent(idGenType)
    }
  }

  def nextId(idEvent: IdEvent): IdsState = idEvent match {
    case NewIdEvent(idGenType, nextId) => copy(ids = ids + (idGenType -> nextId))
    case NextIdEvent(idGenType, nextId) => copy(ids = ids.map(f => if (f._1 == idGenType) (idGenType -> nextId) else f))
  }
}
