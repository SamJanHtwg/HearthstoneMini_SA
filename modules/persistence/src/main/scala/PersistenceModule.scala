package persistence

import persistence.fileIO.FileIOInterface
import persistence.database.DaoInterface
import persistence.database.mongodb.MongoDatabase
import persistence.fileIO.jsonIOImpl.JsonIO
import persistence.database.slick.SlickDatabase

object PersistenceMongoJsonModule {
  given DaoInterface = MongoDatabase
  given FileIOInterface = JsonIO()
}

object PersistenceSlickJsonModule {
  given DaoInterface = SlickDatabase
  given FileIOInterface = JsonIO()
}
