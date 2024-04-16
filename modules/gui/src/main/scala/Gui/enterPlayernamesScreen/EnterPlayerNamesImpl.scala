package gui
package enterPlayernamesScreen

import core.controller.component.ControllerInterface
import core.controller.Strategy
import core.controller.component.controllerImpl.Controller
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import model.Move
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{
  Button,
  Label,
  RadioButton,
  TextField,
  ToggleGroup
}
import scalafx.scene.layout.GridPane
import model.fileIOComponent.jsonIOImpl.FileIO

//noinspection DuplicatedCode
class EnterPlayerNamesImpl(controller: ControllerInterface)
    extends GridPane
    with EnterPlayerNamesScreenInterface {
  override val textfields: Seq[TextField] = Seq.tabulate(2)(index =>
    new TextField() { promptText = s"Player ${index + 1}" }
  )
  override val labels: Seq[Label] =
    Seq.tabulate(2)(index => new Label("Player" + (index + 1)))
  override val nextButton: Button = new Button("next")

  private def updateDisabledState: Unit = {
    nextButton.disable = textfields.exists(_.text.value.trim.isEmpty)
  }

  updateDisabledState

  nextButton.onMouseClicked = _ => {
    controller.setPlayerNames(
      playername1 = textfields.head.text.value,
      playername2 = textfields(1).text.value
    )
  }

  textfields.map(textField =>
    textField.text.onChange { (_, _, newValue) =>
      {
        updateDisabledState
      }
    }
  )

  vgap = 10
  hgap = 10
  padding = Insets(20, 100, 10, 10)

  addColumn(0, labels.head, labels(1))
  addColumn(1, textfields.head, textfields(1))
  add(nextButton, 1, 2)

}
