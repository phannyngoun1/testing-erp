package net.dream.erp.interfaces.dao.part

trait PartComponentSupport { this: PartComponent =>

  trait PartDaoSupport {
    this: DaoSupport[Long, PartRecord] =>
  }
}
