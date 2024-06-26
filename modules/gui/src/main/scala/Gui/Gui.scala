package Gui

import Gui.enterPlayernamesScreen.EnterPlayerNamesImpl
import Gui.mainGameScreen.MainGameScreen
import Gui.modeSelectionScreen.ModeSelectionScreenImpl
import model.GameState
import core.controller.component.controllerImpl.Controller
import javafx.scene.control.DialogPane
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.ButtonType
import scalafx.scene.paint.Color.*
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.{Image, ImageView}
import core.controller.component.ControllerInterface
import util.{Observer, Event}

class Gui(using controller: ControllerInterface) extends JFXApp3, Observer {
  controller.add(this)

  override def update(e: Event, msg: Option[String]): Unit = {
    Platform.runLater {
      e match {
        case Event.ERROR => showErrorDialog(msg)
        case Event.EXIT  => stopApp()
        case Event.PLAY  => start()
      }
    }
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "HearthstoneMini"
      width = 700
      height = 620

      scene = new Scene {
        fill = White
        Platform.runLater {
          () -> {
            controller.field.gameState match {
              case GameState.CHOOSEMODE =>
                content = new ModeSelectionScreenImpl(controller = controller)
              case GameState.ENTERPLAYERNAMES =>
                content = new EnterPlayerNamesImpl(controller = controller)
              case GameState.MAINGAME =>
                content = new MainGameScreen(controller = controller)
              case GameState.WIN =>
                content = new MainGameScreen(controller = controller)
                showWinDialog()
            }
          }
        }
      }
    }
  }

  def showErrorDialog(msg: Option[String]): Unit = {
    val exitButton = new ButtonType("close")
    val alert = new Alert(AlertType.Warning) {
      title = "Unable to proceed!"
      headerText = s"${msg.fold("")(msg => msg)}"
      buttonTypes = Seq(exitButton)
    }

    val result = alert.showAndWait()
    result match {
      case _ => alert.close()
    }
  }

  private def showWinDialog(): Unit = {
    val exitButton = new ButtonType("Exit")
    val alert = new Alert(AlertType.Confirmation) {
      title = "Congratulations!"
      headerText = " "
      contentText =
        s"${controller.getWinner().getOrElse(" ")} has won the Game!"
      graphic = new ImageView(new Image("/gifs/congrats.gif"))
      buttonTypes = Seq(exitButton)
    }

    val result = alert.showAndWait()
    result match {
      case _ => stopApp()
    }
  }
  override def stopApp(): Unit = System.exit(0)
}
