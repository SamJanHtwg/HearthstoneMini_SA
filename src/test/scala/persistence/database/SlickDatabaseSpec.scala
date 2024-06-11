package persistence.database

import org.scalatest.BeforeAndAfterAll
import persistence.database.slick.SlickDatabase
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers

class SlickDatabaseSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach {
    override def beforeEach(): Unit = {
        super.beforeEach()    
    }

    "SlickDatabase" should "initialize the database" in {
        SlickDatabase
    }
}   