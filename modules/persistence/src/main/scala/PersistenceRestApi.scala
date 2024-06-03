package persistence

import fileIO.service.PersistenceService
import database.DaoInterface
import database.slick.SlickDatabase
import database.mongodb.MongoDatabase
import fileIO.FileIOInterface
import fileIO.jsonIOImpl.JsonIO
import _root_.persistence.PersistenceMongoJsonModule

object PersistenceRestApi {
  def main(args: Array[String]): Unit = {
    Starter().start()
  }
}

class Starter() {
  val thread: Thread = new Thread {

    override def run(): Unit = {
      val persistenceService = new PersistenceService(using
        PersistenceSlickJsonModule.given_FileIOInterface,
        PersistenceSlickJsonModule.given_DaoInterface
      )
      persistenceService.start()
    }
  }

  def start(): Unit = {
    thread.start()
  }
}
