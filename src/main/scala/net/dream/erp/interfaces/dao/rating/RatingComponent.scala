package net.dream.erp.interfaces.dao.rating

import net.dream.erp.interfaces.dao.ComponentSupport

trait RatingComponent extends ComponentSupport with RatingComponentSupport {
  import profile.api._

  case class RatingRecord (
    id: Long,
    tranDoc: String,
    deleted: Boolean,
    sequenceNr: Long
  )extends Record

  case class Ratings(tag: Tag) extends TableBase[RatingRecord](tag, "Rating") {

    //def id = column[Long]("id", O.PrimaryKey)
    def tranDoc   = column[String]("tranDoc")
    def deleted    = column[Boolean]("deleted")
    def sequenceNr = column[Long]("sequenceNr")

    override def * =
      (id,tranDoc , deleted, sequenceNr) <> (RatingRecord.tupled, RatingRecord.unapply )
  }

  object RatingDao
    extends TableQuery(Ratings)
    with DaoSupport[Long, RatingRecord]

}
