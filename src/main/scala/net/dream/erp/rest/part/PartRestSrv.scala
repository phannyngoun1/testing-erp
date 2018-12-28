package net.dream.erp.rest.part

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.swagger.annotations._
import javax.ws.rs.Path
import net.dream.erp.domain.inventory.InventoryId
import net.dream.erp.interfaces.service.inventory.InventoryService.Protocol.PartRequest
import net.dream.erp.interfaces.service.setting.IdGeneration.SettingType.PartIdGenType
import net.dream.erp.interfaces.service.setting.IdGeneration.{GetNextIdRequest, GetNextIdResponse}
import net.dream.erp.interfaces.service.setting.UoMSetting.{GetUoMRequest, GetUoMResponse}
import net.dream.erp.rest.RestSupport._
import net.dream.erp.rest.common.ValidateDirectives
import net.dream.erp.rest.part.PartValidateDirectives._
import net.dream.erp.usercase.PartAggregateUseCase
import net.dream.erp.usercase.PartAggregateUseCase.Protocol.AddNewPartSucceeded
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object PartRestSrv {

  case class GetPartRequestJson(id: Long)

  case class NewPartRequestJson(
    partNr: String,
    description: String,
    partTypeId: Int,
    uom: Int
  )

  case class NewPartResponseJson(id: String, errorMessages: Seq[String] = Seq.empty) extends ResponseJson {
    override val isSuccessful: Boolean = errorMessages.isEmpty
  }

  case class UpdatePartRequestJson(
    partNr: String,
    description: String,
    partTypeId: Int,
    uom: Int
  )

  case class UpdatePartResponseJson(id: String, errorMessages: Seq[String] = Seq.empty) extends ResponseJson {
    override val isSuccessful: Boolean = errorMessages.isEmpty
  }
}

@Api(value = "/part", produces = "application/json")
@Path("/part")
class PartRestSrv(useCase: PartAggregateUseCase, settingRoute: ActorRef, aa: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with ValidateDirectives {

  import PartConverter._
  import PartRestSrv._

  implicit val timeout = Timeout(2.seconds)

  //  val myExceptionHandler: ExceptionHandler =
  //    ExceptionHandler {
  //      case _: Exception =>
  //        extractUri { uri =>
  //          println(s"Request to $uri could not be handled normally")
  //          complete(HttpResponse(InternalServerError, entity = "Bad numbers, bad result!!!"))
  //        }
  //    }

  val route = handleRejections(RejectionHandlers.default) {
    //    handleExceptions(myExceptionHandler) {
    defaultPart ~ newPart ~ updatePart
  }
  //}

  def defaultPart =
    path("part" / Segment) { idString =>
      val id = idString.toLong
      get {
        val a = aa.ask(PartRequest(InventoryId(id))).mapTo[AddNewPartSucceeded]
        onSuccess(a) {
          res => complete(res)
        }
      }
    }

  def newPart =
    path("part") {
      post {
        entity(as[NewPartRequestJson]) { json =>
          validateJson(json).apply { validatedJson =>
            val uomFuture = settingRoute ? GetUoMRequest(json.uom)
            val idGenFuture = settingRoute ? GetNextIdRequest(PartIdGenType)
            val addNewPartFuture = for {
              uomRes <- uomFuture.mapTo[GetUoMResponse]
              partId <- idGenFuture.mapTo[GetNextIdResponse]
              partAdded <- useCase.addNewPart(convertToAddNewPartUseCaseModel(partId.id, validatedJson, uomRes.uoM)).mapTo[AddNewPartSucceeded]
            } yield partAdded
            onSuccess(addNewPartFuture) {
              response => complete(response)
            }
          }
        }
      }
    }

    def updatePart =
      path("part" / Segment ) { idString =>
        println(s"id : ${idString}")
        post {
          entity(as[UpdatePartRequestJson]) { json =>
            validateJson(json).apply { validatedJson =>

              val uomFuture = settingRoute ? GetUoMRequest(json.uom)
              val addNewPartFuture = for {
                uomRes <- uomFuture.mapTo[GetUoMResponse]
                partAdded <- useCase.updatePart(convertToUpdatePartUseCaseModel(idString.toLong, validatedJson, uomRes.uoM))
              } yield partAdded
              onSuccess(addNewPartFuture) {
                response => complete(response)
              }
            }
          }
        }
      }
}
