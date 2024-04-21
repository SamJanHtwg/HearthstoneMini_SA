package persistence

import fileIO.service.PersistenceService

object PersistenceRestApi {
    def main(args: Array[String]): Unit = {
        val persistenceService = new PersistenceService()
        persistenceService.start()
    }
}
