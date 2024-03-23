package hearthstoneMini
package aview.Gui.mainGameScreen

import controller.component.ControllerInterface
import controller.Strategy
import controller.component.controllerImpl.Controller
import scalafx.event.EventHandler
import scalafx.scene.input.MouseEvent
import model.Move
import scalafx.geometry.Insets
import javafx.scene.Node
import scalafx.Includes.jfxNode2sfx
import scalafx.scene.control.*
import scalafx.scene.layout.GridPane
import javafx.scene.layout.GridPane.getColumnIndex
import model.cardComponent.cardImpl.Card
import model.fileIOComponent.xmlIOImpl.FileIO
import scalafx.scene.paint.Color.{Black, Blue, Green, Grey, Red, White}
import scalafx.scene.shape.Rectangle
import hearthstoneMini.model.cardComponent.CardInterface

class MainGameScreen(controller: ControllerInterface) extends GridPane {
  vgap = 20
  padding = Insets(20, 100, 10, 10)

  val player1Grid: GridPane = renderPlayer(1)
  val player2Grid: GridPane = renderPlayer(2)

  add(player1Grid, 0,0)
  add(player2Grid, 0,1)
  add(renderButtonGrid(),0,2)

  def renderPlayer(idInt: Int): GridPane = new GridPane() {
    id = idInt.toString
    vgap = 10
    hgap = 10

    val isActive: Boolean = if controller.field.players.head.id.toString == id.value then true else false
    val gamebar: GridPane = new GridPane() {
      vgap = 10
      hgap = 10
      val hpBar: GridPane = new GridPane() {
        id = "hpbar"
        val bar: Rectangle = new Rectangle {
          height = 20
          width = 300 * (controller.field.getPlayerById(idInt).gamebar.hp.value.toDouble /
            controller.field.getPlayerById(idInt).gamebar.hp.max.toDouble)
          fill = Green
        }
        val amount = new Label("  " + controller.field.getPlayerById(idInt).gamebar.hp.value.toString)
        amount.setTextFill(White)
        add(bar, 0,0)
        add(amount,0,0)
      }
      val manaBar: GridPane = new GridPane() {
        val bar: Rectangle = new Rectangle {
          height = 20
          width = 100 * (controller.field.getPlayerById(idInt).gamebar.mana.value.toDouble /
            controller.field.getPlayerById(idInt).gamebar.mana.max.toDouble)
          fill = Blue
        }
        val amount = new Label("  " + controller.field.getPlayerById(idInt).gamebar.mana.value.toString)
        amount.setTextFill(White)
        add(bar, 0,0)
        add(amount,0,0)
      }
      hpBar.onMouseDragReleased = event => {
        val thatNodesX = getColumnIndex(event.getGestureSource.asInstanceOf[Node])
        if event.getGestureSource.asInstanceOf[Node].getParent.getId == "fieldbar" then {
          if event.getSource.asInstanceOf[Node].getParent.getParent.getId == controller.field.players(1).id.toString &&
            event.getGestureSource.asInstanceOf[Node].getParent.getParent.getId == controller.field.players.head.id.toString
          then {
            controller.directAttack(Move(fieldSlotActive = thatNodesX))
          }
        }
      }

      val labelBox: GridPane = new GridPane{
        val box: Rectangle = new Rectangle{
          height = 20
          width = 50
          fill = White
        }
        val label = new Label(controller.field.getPlayerById(idInt).name)
        label.setTextFill(if isActive then Green else Black)
        add(box, 0,0)
        add(label,0,0)
      }

      add(labelBox, 0,0)
      add(hpBar, 1,0)
      add(manaBar,2,0)
    }
    val fieldbar: GridPane = new GridPane() {
      gridLinesVisible = true
      id = "fieldbar"
      vgap = 10
      hgap = 10
    }

    controller.field.getPlayerById(idInt).fieldbar.cardArea.row.zipWithIndex.foreach(card => {
      fieldbar.add(renderCard(card(0)), card(1), 0)
    })
    val hand: GridPane = new GridPane() {
      id = "hand"
      vgap = 10
      hgap = 10
    }
    controller.field.getPlayerById(idInt).gamebar.hand.zipWithIndex.foreach(card => {
      hand.add(renderCard(Some(card(0))), card(1), 0)
    })
    val deck: Rectangle = new Rectangle() {
      fill = Grey
      height = 100
      width = 100
    }
    deck.onMouseClicked = event => {
      if controller.field.getPlayerById(idInt).gamebar.hand.length < 5
        && isActive
      then controller.drawCard()
    }
    val friedhof: Rectangle = new Rectangle() {
      fill = Red
      height = 100
      width = 100
    }
    idInt % 2 match{
      case 0 =>
        add(deck,1,1)
        add(new Label("Deck: " + controller.field.getPlayerById(idInt).gamebar.deck.length.toString),1,1)
        add(friedhof, 1,0)
        add(new Label("Friedhof: " + controller.field.getPlayerById(idInt).gamebar.friedhof.length.toString),1,0)
        add(gamebar, 0,2)
        add(hand, 0,1)
        add(fieldbar, 0, 0)
      case 1 =>
        add(deck,1,1)
        add(new Label("Deck: " + controller.field.getPlayerById(idInt).gamebar.deck.length.toString),1,1)
        add(friedhof, 1,2)
        add(new Label("Friedhof: " + controller.field.getPlayerById(idInt).gamebar.friedhof.length.toString),1,2)
        add(gamebar, 0,0)
        add(hand, 0,1)
        add(fieldbar, 0, 2)
    }
  }

  def renderCard(card: Option[CardInterface]): Node = {
    val background1: Rectangle = new Rectangle() {
      height = 100
      width = 100
      fill = if (card.isDefined) then Grey
      else White
    }

    val mainGrid: GridPane = new GridPane() {
      add(background1, 0,0)
      if (card.isDefined) then {
        val valueGrid: GridPane = new GridPane() {
          val label = new Label(card.get.name)
          val cost = new Label("cost: " + card.get.manaCost.toString)
          val hp = new Label("def: " + card.get.defenseValue.toString)
          val attack = new Label("att: " + card.get.attValue.toString)
          addColumn(0, label, cost, attack, hp)
        }
        add(valueGrid,0,0)
      }
    }
    mainGrid.onDragDetected = (event) => {
      mainGrid.startFullDrag()
    }

    mainGrid.onMouseDragReleased = (event) => {
      val thisNodesX = getColumnIndex(event.getSource.asInstanceOf[Node])
      val thatNodesX = getColumnIndex(event.getGestureSource.asInstanceOf[Node])
      if (event.getSource.asInstanceOf[Node].getParent.getId == "fieldbar" &&
        event.getGestureSource.asInstanceOf[Node].getParent.getId == "hand") then {
        if (event.getSource.asInstanceOf[Node].getParent.getParent.getId == controller.field.players.head.id.toString &&
          event.getGestureSource.asInstanceOf[Node].getParent.getParent.getId == controller.field.players.head.id.toString)
        then {
          controller.placeCard(Move(handSlot = thatNodesX, fieldSlotActive = thisNodesX))
        }
      } else if (event.getSource.asInstanceOf[Node].getParent.getId == "fieldbar" &&
        event.getGestureSource.asInstanceOf[Node].getParent.getId == "fieldbar") then {
        if (event.getSource.asInstanceOf[Node].getParent.getParent.getId == controller.field.players(1).id.toString &&
          event.getGestureSource.asInstanceOf[Node].getParent.getParent.getId == controller.field.players.head.id.toString)
        then {
          controller.attack(Move(fieldSlotInactive = thisNodesX, fieldSlotActive = thatNodesX))
        }
      }
    }
    mainGrid
  }
  def renderButtonGrid(): GridPane = new GridPane {
    val saveButton = new Button("save")
    saveButton.onMouseClicked = (_) => controller.saveField
    hgap = 10
    val switchButton = new Button("end turn")
    switchButton.onMouseClicked = (_) => controller.switchPlayer()
    val undoButton = new Button("undo")
    undoButton.onMouseClicked = (_) => controller.undo
    val redoButton = new Button("redo")
    redoButton.onMouseClicked = (_) => controller.redo

    add(switchButton,0,2)
    add(undoButton,1,2)
    add(redoButton,2,2)
    add(saveButton,3,2)
  }
}

