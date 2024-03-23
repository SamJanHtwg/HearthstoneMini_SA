package hearthstoneMini
package aview.Gui

import aview.Gui.GUI
import controller.GameState
import controller.component.controllerImpl.Controller
import javafx.geometry.Side
import javafx.scene.layout.{BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize}
import scalafx.application.Platform
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.Image
import scalafx.scene.layout.Background
import scalafx.scene.paint.Color
import util.{Event, Observer}
import hearthstoneMini.controller.component.ControllerInterface

class GUIApp(val controller: ControllerInterface) extends Observer {
    override def update(e: Event, msg: Option[String]) = {
        Platform.runLater{
            e match {
                case Event.ERROR => gui.showErrorDialog(msg)
                case Event.EXIT => gui.stopApp()
                case Event.PLAY => gui.start()
            }
        }
    }

    val gui: GUI = new GUI(this, controller)
    val thread: Thread = new Thread {
        override def run(): Unit = {
            gui.main(Array())
        }
    }
    thread.start()

    controller.add(this)
}