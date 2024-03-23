package hearthstoneMini
package aview.Gui.enterPlayernamesScreen

import controller.component.ControllerInterface
import controller.Strategy
import controller.component.controllerImpl.Controller
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.Move
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label, RadioButton, TextField, ToggleGroup}
import scalafx.scene.layout.GridPane
import model.fileIOComponent.jsonIOImpl.FileIO

class EnterPlayernamesScreenImpl(controller: ControllerInterface) extends GridPane with EnterPlayernamesScreenInterface {
  override val textfields: Seq[TextField] = Seq(
    new TextField() { promptText = "Player 1"},
    new TextField() { promptText = "Player 2"})
  override val labels: Seq[Label] = Seq(
    new Label("Player1"),
    new Label("Player2"))
  override val nextButton: Button = new Button("next")

  nextButton.disable = true
  var bool1, bool2: Boolean = false
  nextButton.onMouseClicked = (_) => {
    controller.setPlayerNames(Move(p1 = textfields.head.text.value, p2 = textfields(1).text.value))
  }
  textfields.head.text.onChange { (_, _, newValue) => {
    bool1 = newValue.trim().isEmpty
    nextButton.disable = (bool1 && bool2)
  }}
  textfields(1).text.onChange { (_, _, newValue) => {
    bool2 = newValue.trim().isEmpty
    nextButton.disable = (bool1 && bool2)
  }}

  vgap = 10
  hgap = 10
  padding = Insets(20, 100, 10, 10)

  addColumn(0,labels.head, labels(1))
  addColumn(1, textfields.head, textfields(1))
  add(nextButton, 1,2)

}

