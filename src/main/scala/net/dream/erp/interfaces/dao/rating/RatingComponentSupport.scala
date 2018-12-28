package net.dream.erp.interfaces.dao.rating

trait RatingComponentSupport {
  this: RatingComponent =>

  trait RatingDaoSupport {
    this: DaoSupport[Long, RatingRecord] =>

  }
}
