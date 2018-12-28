package net.dream.erp.domain.uom

import net.dream.erp.domain.inventory.UoM
import net.dream.erp.domain.uom.UoMState.UoMSettingError

trait UoMState {

  def all(): Set[UoM]
  def getUoM(id: Int): Either[UoMSettingError, UoM]
  def addUoM(uoM: UoM): Either[UoMSettingError, UoMState]
}

object UoMState {

  sealed abstract class UoMSettingError(val message: String)
  case class InvalidStateError(id: Option[Int] = None) extends UoMSettingError("Invalid state")
  case class UoMNotFound(id: Int) extends UoMSettingError(s"UoM ${id} not found")
  case class UoMAlreadyExist(uoM: UoM) extends UoMSettingError(s"UoM: ${uoM.name} is already exist")

  def apply(uoMs: Set[UoM]): UoMState = new UoMStateImpl(uoMs)

  def unapply(self: UoMState): Option[Set[UoM]] = Some(self.all())

  private case class UoMStateImpl(
    uoMs: Set[UoM]
  )  extends UoMState {

    override def all(): Set[UoM] = uoMs

    override def getUoM(id: Int)  = {
      val uom = uoMs.filter(_.id == id).headOption
      if(uom.isDefined) Right(uom.get)
      else Left(UoMNotFound(id))
    }

    override def addUoM(uoM: UoM) = {
      if(uoMs.filter(_.id == uoM.id).headOption.isEmpty){
        Right(copy(uoMs = uoMs + uoM))
      }else{
        Left(UoMAlreadyExist(uoM))
      }
    }
  }

}


