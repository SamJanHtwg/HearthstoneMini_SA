package persistence

import fileIO.service.PersistenceService
import _root_.persistence.database.DaoInterface
import _root_.persistence.database.slick.SlickDatabase

object PersistenceRestApi {
  def main(args: Array[String]): Unit = {
    Starter().start()
  }
}

class Starter() {
  val thread: Thread = new Thread {

    override def run(): Unit = {
      val persistenceService = new PersistenceService()
      persistenceService.start()
    }
  }

  def start(): Unit = {
    thread.start()
  }
}
