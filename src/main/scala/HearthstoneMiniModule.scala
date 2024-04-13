package hearthstoneMini

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import controller.*
import controller.component.ControllerInterface
import controller.component.controllerImpl.Controller
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Field
import model.fileIOComponent.FileIOInterface
import model.playerComponent.playerImpl.Player
import model.fileIOComponent.jsonIOImpl.FileIO as json
import model.fileIOComponent.xmlIOImpl.FileIO as xml

class HearthstoneMiniModule extends AbstractModule {
  private val defaultSize = 5

  override def configure(): Unit = {
    bindConstant().annotatedWith(Names.named("DefaultSize")).to(defaultSize)
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    bind(classOf[FieldInterface]).toInstance(new Field())
    bind(classOf[FileIOInterface]).to(classOf[json])
  }
}
