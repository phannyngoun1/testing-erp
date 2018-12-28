package net.dream.erp.rest.add

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import io.swagger.annotations._
import javax.ws.rs.Path
import net.dream.erp.DefaultJsonFormats
import net.dream.erp.rest.add.AddActor.{AddRequest, AddResponse}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

@Api(value = "/add", produces = "application/json")
@Path("/add")
class AddService(addActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  val route = add

  @ApiOperation(value = "Add integers", nickname = "addIntegers", httpMethod = "POST", response = classOf[AddResponse])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "\"numbers\" to sum", required = true,
      dataTypeClass = classOf[AddRequest], paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def add =
    path("add") {
      post {
        entity(as[AddRequest]) { request =>
          complete {

            (addActor ? request).mapTo[AddResponse]
          }
        }
      }
    }

}
