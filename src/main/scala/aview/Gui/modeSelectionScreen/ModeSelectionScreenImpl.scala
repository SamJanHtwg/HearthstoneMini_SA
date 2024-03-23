package hearthstoneMini
package aview.Gui.modeSelectionScreen
import controller.Strategy
import controller.component.ControllerInterface
import controller.component.controllerImpl.Controller
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.fieldComponent.FieldInterface
import model.fileIOComponent.xmlIOImpl.FileIO
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{Button, RadioButton, ToggleGroup}
import scalafx.scene.layout.GridPane

class ModeSelectionScreenImpl(controller: ControllerInterface) extends GridPane with ModeSelectionScreenInterface {
  val fileio = new FileIO
  override val radiobuttons: Seq[RadioButton] = List(new RadioButton("Normal"), new RadioButton("Hardcore"),
    new RadioButton("Debug"))
  override val radiogroup: ToggleGroup = new ToggleGroup()
  radiobuttons.foreach(_.setToggleGroup(radiogroup))
  override val nextbutton: Button = new Button("next")
  nextbutton.onMouseClicked = (_) =>
    controller.setStrategy(radiogroup.getSelectedToggle.getUserData.asInstanceOf[Strategy])

  val loadButton: Button = new Button("load")
  loadButton.onMouseClicked = (_) => controller.loadField

  radiobuttons(0).setUserData(Strategy.normal)
  radiobuttons(1).setUserData(Strategy.hardcore)
  radiobuttons(2).setUserData(Strategy.debug)

  vgap = 10
  hgap = 10
  padding = Insets(20, 100, 10, 10)

  add(radiobuttons(0),0,0)
  add(radiobuttons(1),0,1)
  add(radiobuttons(2),0,2)
  add(nextbutton,1,3)
  add(loadButton, 3,3)

}
