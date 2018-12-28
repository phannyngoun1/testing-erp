package net.dream.erp.interfaces.service.setting

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import enumeratum._
import net.dream.erp.interfaces.service.setting.IdGeneration.IdGenCommandRequest
import net.dream.erp.interfaces.service.setting.UoMSetting.UoMSettingCommandRequest


object SettingRouter {

  def props = Props(new SettingRouter)

  def name = "setting-router"

  sealed trait SettingType extends EnumEntry

  object SettingType extends Enum[SettingType] {

    val values = findValues

    case object UoMSetting extends SettingType
    case object IdGenSetting extends SettingType
  }

}

class SettingRouter extends Actor with ActorLogging {

  import SettingRouter._

  var settings: Map[SettingType, ActorRef] =
    Map(
      SettingType.UoMSetting -> context.actorOf(UoMSetting.props, UoMSetting.name),
      SettingType.IdGenSetting -> context.actorOf(IdGeneration.props, IdGeneration.name)

    )


  override def receive: Receive = {
    case cmd: UoMSettingCommandRequest =>
      settings(SettingType.UoMSetting).forward(cmd)
    case cmd: IdGenCommandRequest =>
      settings(SettingType.IdGenSetting).forward(cmd)
    case _ => throw new NotImplementedError()
  }
}
