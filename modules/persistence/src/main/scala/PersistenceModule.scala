package persistence

import fileIO.FileIOInterface
import database.DaoInterface
import database.mongodb.MongoDBDatabase
import fileIO.jsonIOImpl.JsonIO
import database.slick.SlickDatabase

object PersistenceMongoJsonModule {
  given DaoInterface = new MongoDBDatabase()
  given FileIOInterface = JsonIO()
}

object PersistenceSlickJsonModule {
  given DaoInterface = SlickDatabase
  given FileIOInterface = JsonIO()
}
