package persistence.database

import org.scalatest.BeforeAndAfterAll
import persistence.database.slick.SlickDatabase
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito.mock 
import _root_.slick.jdbc.JdbcBackend

class SlickDatabaseSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach {
    var mockDatabase: JdbcBackend.Database = _

    override def beforeEach(): Unit = {
        super.beforeEach()    
        mockDatabase = mock(classOf[JdbcBackend.Database])
    }

    "SlickDatabase" should "initialize the database" in {
        new SlickDatabase(mockDatabase)
    } 
}   