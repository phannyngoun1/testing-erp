package net.dream.erp.interfaces.serialization

import java.util.Currency

import net.dream.erp.domain.inventory.InventoryEvent.{AddNewPartEvent, InventoryInitialEvent}
import net.dream.erp.domain.inventory.Part._
import net.dream.erp.domain.inventory.UoM.Measurement
import net.dream.erp.domain.inventory._
import net.dream.erp.domain.uom.UoMState
import net.dream.erp.interfaces.service.inventory.InventoryService.Protocol._
import net.dream.erp.interfaces.service.setting.IdGeneration.{GetNextIdRequest, SettingType}
import net.dream.erp.interfaces.service.setting.IdsState
import org.sisioh.baseunits.scala.money.Money
import org.sisioh.baseunits.scala.time.TimePoint

case class AddNewPartRequestJson(partId: Long, partNr: String, description: String, upc: Option[String], partType: Int, uom: UOMJson)

case class UOMJson(id: Int, abbr: String, name: String, description: String, measurement: Int, active: Boolean = true, readonly: Boolean = false)

case class AddNewPartEventJson(partId: Long, partNr: String, description: String, upc: Option[String], partType: Int, uomId: Int, occurredAt: Long)

case class InventoryInitialEventJson(partId: Long, locId: Int, oumId: Int, qty: Float, unitCost: BigDecimal, currency: String, occurredAt: Long)

case class UoMStateJson(uoMs: Set[UOMJson])

case class IdsStateJson(ids: Map[String, Long])

case class TrackingValueJson(trackingTypeId: Int, value: String)

case class PartSizeJson(length: Float, width: Float, height: Float, uomId: Int)

case class PartWeightJson(weight: Float, uomId: Int)

case class PartAccountsJson(assetAcctId: Int, cogsAcctId: Int, adjustmentAcctId: Int, scrapAcctId: Int)

case class PartVendorJson(vendorId: Int, partNr: Option[String], uomId: Int, lastCost: BigDecimal, currency: String, lastDate: Long, default: Boolean)

case class PartJson(
  id: Long,
  nr: String,
  description: String,
  upc: Option[String] = None,
  partTypeId: Int,
  uomId: Int,
  trackingTypes: List[Int],
  size: Option[PartSizeJson] = None,
  weight: Option[PartWeightJson] = None,
  partUoMPickupOnly: Boolean = false,
  url: Option[String] = None,
  details: Option[String] = None,
  accounts: Option[PartAccountsJson] = None,
  defaultLocations: List[Int] = List.empty,
  defaultVendors: List[PartVendorJson] = List.empty,
  dateCreated: Long,
  dateModified: Long,
  lastUserId: Int,
  active: Boolean = true
)

case class SubstituteProductJson(productId: Int, note: String)

case class CostingJson(unitCost: BigDecimal, totalCost: BigDecimal, currency: String, createdAt: Long)

case class ProductJson(
  id: Long,
  nr: String,
  description: String,
  uoM: Option[UoM] = None,
  price: BigDecimal,
  currency: String,
  soldInDiffUoM: Boolean = true,
  taxable: Boolean = true,
  details: Option[String] = None,
  upc: Option[String] = None,
  sku: Option[String] = None,
  soItemTypeId: Option[Int] = None,
  substituteProd: Option[List[SubstituteProductJson]] = None,
  incomeAccount: Option[Int] = None
)

case class StockJson(locId: Int, uomId: Int, qty: Float, date: TimePoint, trackingValue: Option[TrackingValueJson] = None)

case class InventoryModelJson(
  partId: Long,
  avgCosting: Option[CostingJson],
  stdCosting: Option[CostingJson],
  costLayers: Set[CostingJson],
  stocks: List[StockJson]

)

case class GetNextIdRequestJson(idGenType: String)

object InventoryCreatedJson {

  implicit object AddNewPartRequestIso extends ObjToJsonReprIso[AddNewPartRequest, AddNewPartRequestJson] {

    override def convertTo(obj: AddNewPartRequest): AddNewPartRequestJson =
      AddNewPartRequestJson(
        obj.inventoryId.partId,
        obj.newPart.partNr,
        obj.newPart.description,
        obj.newPart.upc,
        obj.newPart.partType.value,
        UOMJson(
          obj.newPart.uom.id,
          obj.newPart.uom.abbr,
          obj.newPart.uom.name,
          obj.newPart.uom.description,
          obj.newPart.uom.measurement.value,
          obj.newPart.uom.active,
          obj.newPart.uom.readonly
        )
      )

    override def convertFrom(json: AddNewPartRequestJson): AddNewPartRequest =
      AddNewPartRequest(
        InventoryId(json.partId),
        newPart = NewPart(
          partNr = json.partNr,
          description = json.description,
          upc = json.upc,
          partType = PartType.withValue(json.partType),
          uom = UoM(
            json.uom.id,
            json.uom.abbr,
            json.uom.name,
            json.uom.description,
            Measurement.withValue(json.uom.measurement),
            json.uom.active,
            json.uom.readonly
          ),
          productInfo = None,
          initialInventory = None,
          defaultLocations = None,
          defaultVendor = None,
          defaultPartAccount = None,
          defaultProductAccount = None
        )
      )
  }


  implicit object InventoryInitialEventIso extends ObjToJsonReprIso[InventoryInitialEvent, InventoryInitialEventJson] {

    override def convertTo(obj: InventoryInitialEvent): InventoryInitialEventJson =
      InventoryInitialEventJson(
        obj.inventoryId.partId,
        obj.location,
        obj.uoM,
        obj.qty,
        obj.unitCost.amount,
        obj.unitCost.currency.getCurrencyCode,
        obj.occurredAt.millisecondsFromEpoc
      )

    override def convertFrom(json: InventoryInitialEventJson): InventoryInitialEvent =
      InventoryInitialEvent(
        InventoryId(json.partId),
        json.locId,
        json.oumId,
        json.qty,
        Money.apply(json.unitCost, Currency.getInstance(json.currency)),
        TimePoint.from(json.occurredAt)
      )
  }

  implicit object AddNewPartEventIso extends ObjToJsonReprIso[AddNewPartEvent, AddNewPartEventJson] {

    override def convertTo(event: AddNewPartEvent): AddNewPartEventJson =

      AddNewPartEventJson(
        event.inventoryId.partId,
        event.partNr,
        event.description,
        event.upc,
        event.partTypeId,
        event.uomId,
        occurredAt = event.occurredAt.millisecondsFromEpoc
      )

    override def convertFrom(json: AddNewPartEventJson): AddNewPartEvent =
      AddNewPartEvent(
        InventoryId(json.partId),
        partNr = json.partNr,
        description = json.description,
        upc = json.upc,
        partTypeId = json.partType,
        uomId = json.uomId,
        productInfo = None,
        initialInventory = None,
        defaultLocations = None,
        defaultVendor = None,
        defaultPartAccount = None,
        defaultProductAccount = None,
        occurredAt = TimePoint.from(json.occurredAt)
      )
  }

  implicit object UoMIso extends ObjToJsonReprIso[UoM, UOMJson] {

    override def convertTo(obj: UoM): UOMJson =
      UOMJson(
        id = obj.id,
        abbr = obj.abbr,
        name = obj.name,
        description = obj.description,
        measurement = obj.measurement.value,
        active = obj.active,
        readonly = obj.readonly
      )

    override def convertFrom(json: UOMJson): UoM =
      UoM(
        id = json.id,
        abbr = json.abbr,
        name = json.name,
        description = json.description,
        measurement = Measurement.withValue(json.measurement),
        active = json.active,
        readonly = json.readonly
      )
  }


  implicit object UoMStateIso extends ObjToJsonReprIso[UoMState, UoMStateJson] {

    override def convertTo(obj: UoMState): UoMStateJson =
      UoMStateJson(
        uoMs = obj.all().map(uom => UOMJson(uom.id, uom.abbr, uom.name, uom.description, uom.measurement.value, uom.active, uom.readonly))
      )

    override def convertFrom(json: UoMStateJson): UoMState =
      UoMState(json.uoMs.map(json => UoM(json.id, json.abbr, json.name, json.description, Measurement.withValue(json.measurement), json.active, json.readonly)))
  }

  implicit object IdsStateIso extends ObjToJsonReprIso[IdsState, IdsStateJson] {

    override def convertTo(obj: IdsState): IdsStateJson =
      IdsStateJson(
        ids = obj.getIds.map(f => f._1.entryName -> f._2)
      )

    override def convertFrom(json: IdsStateJson): IdsState =
      IdsState(json.ids.map(f => SettingType.withName(f._1) -> f._2))
  }

  implicit object GetNextIdRequestIso extends ObjToJsonReprIso[GetNextIdRequest, GetNextIdRequestJson] {
    override def convertTo(obj: GetNextIdRequest): GetNextIdRequestJson =
      GetNextIdRequestJson(
        obj.idGenType.entryName
      )

    override def convertFrom(json: GetNextIdRequestJson): GetNextIdRequest =
      GetNextIdRequest(SettingType.withName(json.idGenType))
  }

  implicit object InventoryModelIso extends ObjToJsonReprIso[InventoryModel, InventoryModelJson] {

    override def convertTo(obj: InventoryModel): InventoryModelJson =

      InventoryModelJson(
        partId = obj.id.partId,
        avgCosting = obj.avgCosting.map(AvgCostingIso.convertTo),
        stdCosting = obj.stdCosting.map(StdCostingIso.convertTo),
        costLayers = obj.costLayers.map(CostLayerIso.convertTo),
        stocks = obj.stocks.map(StockIso.convertTo)
      )

    override def convertFrom(json: InventoryModelJson): InventoryModel =
      InventoryModel(
        id = InventoryId(partId = json.partId),
        avgCosting = json.avgCosting.map(AvgCostingIso.convertFrom),
        stdCosting = json.stdCosting.map(StdCostingIso.convertFrom),
        costLayers = json.costLayers.map(CostLayerIso.convertFrom),
        stocks = json.stocks.map(StockIso.convertFrom)
      )
  }

  implicit object PartIso extends ObjToJsonReprIso[Part, PartJson] {
    override def convertTo(obj: Part): PartJson =
      PartJson(
        id = obj.id,
        nr = obj.nr,
        description = obj.description,
        upc = obj.upc,
        partTypeId = obj.partType.value,
        uomId = obj.uom.id,
        trackingTypes = obj.trackingTypes.map(_.value),
        size = obj.size.map(PartSizeIso.convertTo),
        weight = obj.weight.map(PartWeightIso.convertTo),
        partUoMPickupOnly = obj.partUoMPickupOnly,
        url = obj.url.map(_.toString),
        details = obj.details,
        accounts = obj.accounts.map(PartAccountsIso.convertTo),
        defaultLocations = obj.defaultLocations.map(_.id),
        defaultVendors = obj.defaultVendors.map(PartVendorIso.convertTo),
        dateCreated = obj.createdAt.millisecondsFromEpoc,
        dateModified = obj.modifiedAt.millisecondsFromEpoc,
        lastUserId = obj.lastUser.id,
        active = obj.active
      )

    override def convertFrom(json: PartJson): Part = ???
//      Part(
//        id = json.id,
//        nr = json.nr,
//        description = json.description,
//        upc = json.upc,
//    partType = PartType.withValue(json.partTypeId),
//    uom: UoM,
//    trackingTypes: List[TrackingType] = List.empty,
//    size: Option[PartSize] = None,
//    weight: Option[PartWeight] = None,
//    partUoMPickupOnly: Boolean = false,
//    url: Option[URL] = None,
//    details: Option[String] = None,
//    accounts: Option[PartAccounts] = None,
//    defaultLocations: List[Location] = List.empty,
//    defaultVendors: List[PartVendor] = List.empty,
//
//    createdAt: TimePoint = Clock.now,
//    modifiedAt: TimePoint = Clock.now,
//    lastUser: User = admin,
//
//    active: Boolean = true
//      )
  }

  implicit object ProductIso extends ObjToJsonReprIso[Product, ProductJson] {
    override def convertTo(obj: Product): ProductJson = ???

    override def convertFrom(json: ProductJson): Product = ???
  }

  implicit object AvgCostingIso extends ObjToJsonReprIso[AvgCosting, CostingJson] {

    override def convertTo(obj: AvgCosting): CostingJson = ???

    override def convertFrom(json: CostingJson): AvgCosting = ???
  }

  implicit object StdCostingIso extends ObjToJsonReprIso[StdCosting, CostingJson] {

    override def convertTo(obj: StdCosting): CostingJson = ???

    override def convertFrom(json: CostingJson): StdCosting = ???
  }

  implicit object CostLayerIso extends ObjToJsonReprIso[CostLayer, CostingJson] {
    override def convertTo(obj: CostLayer): CostingJson = ???

    override def convertFrom(json: CostingJson): CostLayer = ???
  }

  implicit object StockIso extends ObjToJsonReprIso[Stock, StockJson] {
    override def convertTo(obj: Stock): StockJson = ???

    override def convertFrom(json: StockJson): Stock = ???
  }

  implicit object PartSizeIso extends ObjToJsonReprIso[PartSize, PartSizeJson] {
    override def convertTo(obj: PartSize): PartSizeJson = ???

    override def convertFrom(json: PartSizeJson): PartSize = ???
  }

  implicit object PartWeightIso extends ObjToJsonReprIso[PartWeight, PartWeightJson] {
    override def convertTo(obj: PartWeight): PartWeightJson = ???

    override def convertFrom(json: PartWeightJson): PartWeight = ???
  }

  implicit object PartAccountsIso extends ObjToJsonReprIso[PartAccounts, PartAccountsJson] {
    override def convertTo(obj: PartAccounts): PartAccountsJson = ???

    override def convertFrom(json: PartAccountsJson): PartAccounts = ???
  }

  implicit object PartVendorIso extends ObjToJsonReprIso[PartVendor, PartVendorJson] {
    override def convertTo(obj: PartVendor): PartVendorJson = ???

    override def convertFrom(json: PartVendorJson): PartVendor = ???
  }

}
