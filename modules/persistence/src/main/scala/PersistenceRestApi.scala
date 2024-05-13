package persistence

import fileIO.service.PersistenceService
import _root_.persistence.database.DaoInterface
import _root_.persistence.database.slick.SlickDatabase

object PersistenceRestApi {
  given DaoInterface = new SlickDatabase()
  
  def main(args: Array[String]): Unit = {
    val persistenceService = new PersistenceService()
    persistenceService.start()
  }
}
