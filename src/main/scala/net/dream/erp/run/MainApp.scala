package net.dream.erp.run

import net.dream.erp.readsides.operdb.ReadSideApp

object MainApp {

  def main(args: Array[String]): Unit = {

    args.headOption match {
      case None =>
        startClusterInSameJvm()
      case Some(portString) if portString.matches("""\d+""") =>
      //TODO: to be implemented
    }
  }

  def startClusterInSameJvm(): Unit = {
    Backend.main(Seq("2551").toArray)
    Backend.main(Seq("2552").toArray)
    ReadSide.main(Seq("2553").toArray)
    Frontend.main(Seq("3000").toArray)
  }

}
