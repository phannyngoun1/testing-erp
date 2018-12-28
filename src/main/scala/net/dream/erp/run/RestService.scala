package net.dream.erp.run

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import net.dream.erp.interfaces.service.inventory.InventoryAggregateFlowsImpl
import net.dream.erp.interfaces.service.part.PartAggregateFlowsImpl
import net.dream.erp.interfaces.service.setting.SettingRouter
import net.dream.erp.interfaces.service.sharding.ShardedServices
import net.dream.erp.rest.ApiServerConfig
import net.dream.erp.rest.add.{AddActor, AddService}
import net.dream.erp.rest.addoption.{AddOptionActor, AddOptionService}
import net.dream.erp.rest.echoenum.EchoEnumService
import net.dream.erp.rest.hello.{HelloActor, HelloService}
import net.dream.erp.rest.inventory.InventoryRestSrv
import net.dream.erp.rest.part.PartRestSrv
import net.dream.erp.swagger.SwaggerDocService
import net.dream.erp.usercase.{InventoryAggregateUseCase, PartAggregateUseCase}
import pureconfig.loadConfigOrThrow

import scala.util.{Failure, Success}

object RestService extends RouteConcatenation {
  def start(implicit system: ActorSystem): Unit = {

    val ApiServerConfig(host, port) =
      loadConfigOrThrow[ApiServerConfig](system.settings.config.getConfig("erp.api-server"))

    implicit val mat = ActorMaterializer()
    implicit val ex = system.dispatcher

    val add = system.actorOf(Props[AddActor])
    val addOption = system.actorOf(Props[AddOptionActor])
    val hello = system.actorOf(Props[HelloActor])

    implicit val settingRouter = system.actorOf(SettingRouter.props, SettingRouter.name)
    val shardedServices = system.actorOf(ShardedServices.props, ShardedServices.name)
    val partAggregateFlows = new PartAggregateFlowsImpl(shardedServices)
    val inventoryAggregateFlow = new InventoryAggregateFlowsImpl(shardedServices)

    val partAggregateUseCase = new PartAggregateUseCase(partAggregateFlows, inventoryAggregateFlow)
    val inventoryAggregateUseCase = new InventoryAggregateUseCase(inventoryAggregateFlow)

    val routes =
      cors()(new AddService(add).route ~
        new AddOptionService(addOption).route ~
        new HelloService(hello).route ~
        new PartRestSrv(partAggregateUseCase, settingRouter, shardedServices).route ~
        new InventoryRestSrv(inventoryAggregateUseCase, settingRouter).route ~
        EchoEnumService.route ~
        SwaggerDocService.routes)
    val bindingFuture = Http().bindAndHandle(routes, host, port)
    val log = Logging(system.eventStream, "Job synchronization")
    bindingFuture.map { serverBinding =>
      log.info(s"Rest API bound to ${serverBinding.localAddress} ")
    }.onComplete {
      case Success(_) => log.info("API started")
      case Failure(t) =>
        log.error(t, "Failed to bind to {}:{}!", host, port)
        system.terminate()
    }
  }
}
