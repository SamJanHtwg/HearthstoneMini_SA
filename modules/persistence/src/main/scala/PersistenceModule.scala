package persistence

import fileIO.FileIOInterface
import database.DaoInterface
import database.mongodb.MongoDatabase
import fileIO.jsonIOImpl.JsonIO
import database.slick.SlickDatabase

object PersistenceMongoJsonModule {
  given DaoInterface = MongoDatabase
  given FileIOInterface = JsonIO()
}

object PersistenceSlickJsonModule {
  given DaoInterface = SlickDatabase
  given FileIOInterface = JsonIO()
}
