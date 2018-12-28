package net.dream.erp.rest.inventory

import net.dream.erp.rest.inventory.InventoryRestSrv._
import net.dream.erp.usercase.InventoryAggregateUseCase
import net.dream.erp.rest.RestSupport.ControllerBase._
import net.dream.erp.rest.part.InvalidEncodeIdException
import org.hashids.Hashids

object InventoryConverter {

  val convertToGetInventoryUseCaseModel: (GetInventoryRequestJson) =>  InventoryAggregateUseCase.Protocol.GetInventoryRequest = {

    case (json) =>
      InventoryAggregateUseCase.Protocol.GetInventoryRequest(json.id)
  }

  def convertToGetInventoryInterfaceModel(
    implicit hashIds: Hashids
  ): PartialFunction[InventoryAggregateUseCase.Protocol.GetInventoryResponse, GetInventoryResponseJson] = {
    case response: InventoryAggregateUseCase.Protocol.GetInventorySucceeded =>
      response.id.encodeToHashId.fold ({ ex =>
        throw InvalidEncodeIdException(response.id, Some(ex))
      }, { partId =>
        GetInventoryResponseJson(partId, response.qty)
      })
  }


}
