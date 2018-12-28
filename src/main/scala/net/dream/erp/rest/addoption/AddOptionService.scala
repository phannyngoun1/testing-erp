package net.dream.erp.rest.addoption

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import io.swagger.annotations._
import javax.ws.rs.Path
import net.dream.erp.DefaultJsonFormats
import net.dream.erp.rest.addoption.AddOptionActor._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Api(value = "/add", produces = "application/json")
@Path("/addOption")
class AddOptionService(addActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  implicit val requestFormat = jsonFormat2(AddOptionRequest)
  implicit val responseFormat = jsonFormat1(AddOptionResponse)

  val route = addOption

  @ApiOperation(value = "Add integers", nickname = "addIntegers", httpMethod = "POST", response = classOf[AddOptionResponse])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "\"numbers\" to sum", required = true,
      dataTypeClass = classOf[AddOptionRequest], paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def addOption =
    path("addOption") {
      post {
        entity(as[AddOptionRequest]) { request =>
          complete {
            (addActor ? request).mapTo[AddOptionResponse]
          }
        }
      }
    }

}
