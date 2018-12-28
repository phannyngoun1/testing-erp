package net.dream.erp.domain.inventory

import net.dream.erp.domain.inventory.PartTracking.TrackingValue
import net.dream.erp.domain.location.Location
import org.sisioh.baseunits.scala.time.TimePoint

case class Stock(location: Location, uoM: UoM, qty: Float , date: TimePoint, partTrackingValue: Option[TrackingValue] = None)