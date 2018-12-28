package net.dream.erp.domain

object Common {

  trait ObjectType {

    def getId: Int

    def getName: String
  }


  case class PreLoad[A](id: Int, loaded: A)

}
