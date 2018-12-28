package net.dream.erp.rest.hello

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import io.swagger.annotations._
import javax.ws.rs.Path
import net.dream.erp.DefaultJsonFormats
import net.dream.erp.rest.RestSupport.ControllerBase._
import net.dream.erp.rest.hello.HelloActor.{AnonymousHello, Greeting, Hello}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Api(value = "/hello", produces = "application/json")
@Path("/hello")
class HelloService(hello: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)
  implicit val greetingFormat = jsonFormat1(Greeting)

  val route =
    getHello ~
      getHelloSegment ~
      getHashId

  @ApiOperation(value = "Return Hello greeting", nickname = "anonymousHello", httpMethod = "GET", response = classOf[Greeting])
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHello =
    path("hello") {
      get {
        complete {
          (hello ? AnonymousHello).mapTo[Greeting]
        }
      }
    }

  @Path("/{name}")
  @ApiOperation(value = "Return Hello greeting with person's name", notes = "", nickname = "hello", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Name of person to greet", required = false, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[Greeting]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHelloSegment =
    path("hello" / Segment) { name =>
      get {
        complete {
          (hello ? Hello(name)).mapTo[Greeting]
        }
      }
    }

  @Path("/{id}/toHashId")
  @ApiOperation(value = "Return hash id from id(long)", notes = "", nickname = "hashId", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "hashId/id", value = "id: long", required = false, dataType = "long", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return hash id", response = classOf[Greeting]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHashId =
    path("hello" / "hashid" / Segment) { idString =>

      val id = idString.toLong
      get {
        complete {
          s"${id} to ${id.encodeToHashId}"
        }
      }
    }

}

