package net.dream.erp.rest.part

import akka.japi.Option.Some
import net.dream.erp.domain.inventory.Part._
import net.dream.erp.domain.inventory.{NewPart, UoM}
import net.dream.erp.rest.part.PartRestSrv._
import net.dream.erp.usercase.PartAggregateUseCase.Protocol._
import org.hashids.Hashids
import net.dream.erp.rest.RestSupport.ControllerBase._

case class InvalidEncodeIdException(id: Long, cause: Option[Throwable])
  extends Exception(s"Failed to encode id: $id")

object PartConverter {

  val convertToAddNewPartUseCaseModel: (Long, NewPartRequestJson, UoM) => AddNewPartRequest = {
    case (id, json, uoM) => {
      AddNewPartRequest(
        id = id,
        newPart = NewPart(
          partNr = json.partNr,
          description = json.description,
          upc = None,
          partType = PartType.Inventory,
          uom = uoM,
          productInfo = None,
          initialInventory = None,
          defaultLocations = None,
          defaultVendor = None,
          defaultPartAccount = None,
          defaultProductAccount = None
        )
      )
    }
  }

  val convertToUpdatePartUseCaseModel:(Long, UpdatePartRequestJson, UoM) => UpdatePartRequest = {
    case (id, json, uoM) =>
      UpdatePartRequest(
        id = id,
        partNr = json.partNr,
        description = json.description,
        upc = None,
        partType = PartType.withValue(json.partTypeId) ,
        uom = uoM
      )
  }

  def convertToUpdatePartInterfaceModel(
    implicit hashIds: Hashids
  ): PartialFunction[UpdatePartResponse, UpdatePartResponseJson] = {
    case rs: UpdatePartSucceeded =>
      rs.id.encodeToHashId.fold({ ex =>
        throw InvalidEncodeIdException(rs.id, Some(ex))

      }, {id =>
        UpdatePartResponseJson(id)
      })

    case rs: UpdatePartFailed =>
      rs.id.encodeToHashId.fold({ ex =>
        throw InvalidEncodeIdException(rs.id, Some(ex))
      },{ id =>
        UpdatePartResponseJson(id, Seq(rs.ex.message))
      })
  }

}
