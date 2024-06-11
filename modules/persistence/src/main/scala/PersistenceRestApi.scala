package persistence

import fileIO.service.PersistenceService
import database.DaoInterface
import database.slick.SlickDatabase
import fileIO.FileIOInterface
import fileIO.jsonIOImpl.JsonIO

object PersistenceRestApi {
  def main(args: Array[String]): Unit = {
    Starter().start()
  }
}

class Starter() {
  val thread: Thread = new Thread {

    override def run(): Unit = {
      val persistenceService = new PersistenceService(using
        PersistenceMongoJsonModule.given_FileIOInterface,
        PersistenceMongoJsonModule.given_DaoInterface
      )
      persistenceService.start()
    }
  }

  def start(): Unit = {
    thread.start()
  }
}
