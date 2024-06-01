package persistence

import fileIO.service.PersistenceService
import persistence.database.DaoInterface
import persistence.database.slick.SlickDatabase
import persistence.database.mongodb.MongoDatabase
import persistence.fileIO.FileIOInterface
import persistence.fileIO.jsonIOImpl.JsonIO
import persistence.PersistenceMongoJsonModule
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
