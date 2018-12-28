package net.dream.erp.rest.inventory

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import io.swagger.annotations.Api
import javax.ws.rs.Path
import net.dream.erp.rest.RestSupport._
import net.dream.erp.usercase.InventoryAggregateUseCase
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object InventoryRestSrv {

  case class GetInventoryRequestJson(id: Long)

  case class GetInventoryResponseJson(id: String, qty: Float, errorMessages: Seq[String] = Seq.empty) extends ResponseJson {
    override val isSuccessful: Boolean = errorMessages.isEmpty
  }

}

@Api(value = "/inventory", produces = "application/json")
@Path("/inventory")
class InventoryRestSrv(useCase: InventoryAggregateUseCase, settingRoute: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with InventoryValidateDirectives {

  import InventoryConverter._
  import InventoryRestSrv._
  import net.dream.erp.rest.RestSupport.ControllerBase._

  implicit val timeout = Timeout(2.seconds)

  val route = handleRejections(RejectionHandlers.default) {
    getInventory
  }

  def getInventory =
    path("inventory" / Segment) { idString =>

      println(s" get inventory id =  ${idString}")

      get {
        validatePartId(idString) { id =>
          val future = useCase.getInventory(convertToGetInventoryUseCaseModel(GetInventoryRequestJson(id))).
            map(convertToGetInventoryInterfaceModel)
          onSuccess(future) { response =>
            complete(response)
          }
        }
      }
    }

}
