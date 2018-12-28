package net.dream.erp.interfaces.dao.part

import net.dream.erp.interfaces.dao.ComponentSupport

trait PartComponent extends ComponentSupport with PartComponentSupport {

  import profile.api._

  case class PartRecord(
    id: Long,
    deleted: Boolean,
    sequenceNr: Long
  ) extends Record

  case class Parts(tag: Tag) extends TableBase[PartRecord](tag, "part") {

    def deleted = column[Boolean]("deleted")

    def sequenceNr = column[Long]("sequence_nr")


    override def * =
      (id, deleted, sequenceNr) <> (PartRecord.tupled, PartRecord.unapply)
  }

  object PartDao
    extends TableQuery(Parts)
      with DaoSupport[Long, PartRecord]
      with PartDaoSupport

}
