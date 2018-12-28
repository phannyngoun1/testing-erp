package net.dream.erp.swagger

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import io.swagger.models.ExternalDocs
import io.swagger.models.auth.BasicAuthDefinition
import net.dream.erp.rest.add.AddService
import net.dream.erp.rest.addoption.AddOptionService
import net.dream.erp.rest.echoenum.EchoEnumService
import net.dream.erp.rest.hello.HelloService

object SwaggerDocService extends SwaggerHttpService {
  override val apiClasses = Set(classOf[AddService], classOf[AddOptionService], classOf[HelloService], EchoEnumService.getClass)
  override val host = "localhost:12345"
  override val info = Info(version = "1.0")
  override val externalDocs = Some(new ExternalDocs("Core Docs", "http://acme.com/docs"))
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}