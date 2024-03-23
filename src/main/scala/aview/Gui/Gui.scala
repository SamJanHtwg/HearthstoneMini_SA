package hearthstoneMini
package aview.Gui

import aview.Gui.enterPlayernamesScreen.EnterPlayernamesScreenImpl
import aview.Gui.mainGameScreen.MainGameScreen
import aview.Gui.modeSelectionScreen.ModeSelectionScreenImpl
import controller.GameState
import controller.component.controllerImpl.Controller
import javafx.scene.control.DialogPane
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.control.ButtonType
import scalafx.scene.paint.Color.*
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.{Image, ImageView}
import hearthstoneMini.controller.component.ControllerInterface


class GUI(guiApp: GUIApp, controller: ControllerInterface) extends JFXApp3
{
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "HearthstoneMini"
      width = 700
      height = 620

      scene = new Scene {
        fill = White
        Platform.runLater {
          () -> {
            controller.gameState match {
              case GameState.CHOOSEMODE => content = new ModeSelectionScreenImpl(controller = controller)
              case GameState.ENTERPLAYERNAMES => content = new EnterPlayernamesScreenImpl(controller = controller)
              case GameState.MAINGAME => content = new MainGameScreen(controller = controller)
              case GameState.WIN => {
                content =  new MainGameScreen(controller = controller)
                showWinDialog
              }
            }
          }
        }
      }
    }
  }

  def showErrorDialog(msg: Option[String]) = {
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

  
  def showWinDialog: Unit = {
    val exitButton = new ButtonType("Exit")
    val alert = new Alert(AlertType.Confirmation) {
      title = "Congratulations!"
      headerText = " "
      contentText = s"${controller.getWinner().getOrElse(" ")} has won the Game!"
      graphic = new ImageView(new Image("/gifs/congrats.gif"))
      buttonTypes = Seq(exitButton)
    }

    val result = alert.showAndWait()
    result match {
      case _ => stopApp()
    }
  }
  override def stopApp():Unit = System.exit( 0 )
}



