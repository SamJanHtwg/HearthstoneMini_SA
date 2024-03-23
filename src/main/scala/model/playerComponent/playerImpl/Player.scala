package hearthstoneMini
package model.playerComponent.playerImpl

import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.{Field, FieldObject}
import model.gamebarComponent.gamebarImpl.Gamebar
import model.playerComponent.PlayerInterface
import model.fieldbarComponent.fieldbarImpl.Fieldbar
import model.fieldComponent.fieldImpl.{Field, FieldObject}
import model.fieldComponent.FieldInterface
import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.*
import aview.Strings

import java.awt.MenuBar
import scala.xml.Node
import hearthstoneMini.model.fieldbarComponent.FieldbarInterface
import hearthstoneMini.model.gamebarComponent.GamebarInterface

object Player {
  def fromJson(json: JsValue): Player = Player(
    name = (json \\ "name").head.toString.replace("\"", ""),
    id = (json \\ "id").head.toString().toInt,
    fieldbar = Fieldbar.fromJson((json \\ "fieldbar").head),
    gamebar = Gamebar.fromJson((json \\ "gamebar").head)
  )

  def fromXML(node: Node): Player = Player(
    name = (node \\ "name").head.text,
    id = (node \\ "id").head.text.toInt,
    fieldbar = Fieldbar.fromXML((node \\ "fieldbar").head),
    gamebar = Gamebar.fromXML((node \\ "gamebar").head)
  )
}

case class Player(name: String = "Player", id: Int,
                  fieldbar: FieldbarInterface = new Fieldbar(FieldObject.standartSlotNum, None),
                  gamebar: GamebarInterface = new Gamebar())
  extends PlayerInterface {
  override def placeCard(handSlot: Int, fieldSlot: Int): Player = copy(
    fieldbar = fieldbar.placeCard(fieldSlot, gamebar.hand(handSlot)),
    gamebar = gamebar.removeCardFromHand(handSlot))

  override def drawCard(): Player = copy(gamebar = gamebar.drawCard())

  override def destroyCard(fieldSlot: Int): Player = copy(
    fieldbar = fieldbar.removeCard(fieldSlot),
    gamebar = gamebar.addCardToFriedhof(fieldbar.cardArea.row(fieldSlot)))

  override def reduceHp(amount: Int): Player = copy(gamebar = gamebar.reduceHp(amount))

  override def increaseHp(amount: Int): Player = copy(gamebar = gamebar.increaseHp(amount))

  override def reduceMana(amount: Int): Player = copy(gamebar = gamebar.reduceMana(amount))

  override def increaseMana(amount: Int): Player = copy(gamebar = gamebar.increaseMana(amount))

  override def resetAndIncreaseMana(): Player = copy(gamebar = gamebar.resetAndIncreaseMana())

  override def setName(name: String): Player = copy(name = name)

  override def setHpValue(amount: Int): Player = copy(gamebar = gamebar.setHpValue(amount))

  override def setManaValue(amount: Int): Player = copy(gamebar = gamebar.setManaValue(amount))

  override def reduceAttackCount(slotNum: Int): Player = copy(fieldbar = fieldbar.reduceAttackCount(slotNum))

  override def resetAttackCount(): Player = copy(fieldbar = fieldbar.resetAttackCount())

  override def toMatrix: Matrix[String] = if ((id % 2) == 1) then renderUnevenId() else renderEvenId()

  override def renderUnevenId(): Matrix[String] = new Matrix[String](
    FieldObject.standartMenueBarHeight + FieldObject.standartGameBarHeight + FieldObject.standartFieldBarHeight,
    FieldObject.standartFieldWidth,
    " ")
    .updateMatrixWithMatrix(0, 0, menueBar())
    .updateMatrixWithMatrix(FieldObject.standartMenueBarHeight, 0, gamebar.toMatrix)
    .updateMatrixWithMatrix(FieldObject.standartGameBarHeight + FieldObject.standartMenueBarHeight, 0,
      fieldbar.toMatrix)

  override def renderEvenId(): Matrix[String] = new Matrix[String](
    FieldObject.standartMenueBarHeight + FieldObject.standartGameBarHeight + FieldObject.standartFieldBarHeight,
    FieldObject.standartFieldWidth, " ")
    .updateMatrixWithMatrix(0, 0, fieldbar.toMatrix)
    .updateMatrixWithMatrix(FieldObject.standartFieldBarHeight, 0, gamebar.toMatrix)
    .updateMatrixWithMatrix(FieldObject.standartFieldBarHeight + FieldObject.standartGameBarHeight,
      0, menueBar())

  override def reduceDefVal(slotNum: Int, amount: Int): Player = copy(
    fieldbar = fieldbar.reduceDefVal(slotNum, amount))

  override def menueBar(): Matrix[String] = new Matrix[String](FieldObject.standartMenueBarHeight,
    FieldObject.standartFieldWidth, " ")
    .updateMatrix(0, 0, List[String](name + " " +
      "#" * ((FieldObject.standartFieldWidth - name.length - 1) *
        gamebar.hp.value / gamebar.hp.max).asInstanceOf[Float].floor.asInstanceOf[Int], "-" *
      FieldObject.standartFieldWidth))


  override def toJson: JsValue = Json.obj(
    "name" -> name,
    "id" -> id,
    "fieldbar" -> fieldbar.toJson,
    "gamebar" -> gamebar.toJson
  )

  override def toXML: Node =
    <Player>
      <name>
        {name}
      </name>
      <id>
        {id.toString}
      </id>
      <gamebar>
        {gamebar.toXML}
      </gamebar>
      <fieldbar>
        {fieldbar.toXML}
      </fieldbar>
    </Player>
}