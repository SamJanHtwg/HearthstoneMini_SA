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
import hearthstoneMini.model.cardComponent.CardInterface

import java.awt.MenuBar
import scala.xml.Node
import hearthstoneMini.model.fieldbarComponent.FieldbarInterface
import hearthstoneMini.model.gamebarComponent.GamebarInterface

/** TODO:
 *    - Serialisierbarkeit Mana
 *    - Serialisierbarkeit Hp
 * */


object Player {
  def fromJson(json: JsValue): Player = Player(
    name = (json \\ "name").head.toString.replace("\"", ""),
    hpValue = 0,
    maxHpValue = 0,
    id = (json \\ "id").head.toString().toInt,
    manaValue = 0,
    maxManaValue = 1,
    fieldbar = Fieldbar.fromJson((json \\ "fieldbar").head),
    gamebar = Gamebar.fromJson((json \\ "gamebar").head)
  )

  def fromXML(node: Node): Player = Player(
    name = (node \\ "name").head.text,
    hpValue = 0,
    maxHpValue = 0,
    id = (node \\ "id").head.text.toInt,
    manaValue = 0,
    maxManaValue = 1,
    fieldbar = Fieldbar.fromXML((node \\ "fieldbar").head),
    gamebar = Gamebar.fromXML((node \\ "gamebar").head)
  )
}

case class Player(name: String = "Player",
                  id: Int,
                  hpValue: Int = 5,
                  maxHpValue: Int = 1,
                  maxManaValue: Int = 1,
                  manaValue: Int = 1,
                  fieldbar: FieldbarInterface = new Fieldbar(FieldObject.standartSlotNum, None),
                  gamebar: GamebarInterface = new Gamebar())

  extends PlayerInterface {
  override def placeCard(handSlot: Int, fieldSlot: Int): Player = copy(
    fieldbar = fieldbar.placeCard(fieldSlot, gamebar.hand(handSlot)),
    gamebar = gamebar.removeCardFromHand(handSlot))

  // player
  override def drawCard(): Player = copy(gamebar = gamebar.drawCard())

  override def reduceAttackCount(slotNum: Int): Player = copy(fieldbar = fieldbar.reduceAttackCount(slotNum))

  override def resetAttackCount(): Player = copy(fieldbar = fieldbar.resetAttackCount())

  override def destroyCard(fieldSlot: Int): Player = copy(
    fieldbar = fieldbar.removeCard(fieldSlot),
    gamebar = gamebar.addCardToFriedhof(fieldbar.cardArea.row(fieldSlot)))

  override def setName(name: String): Player = copy(name = name)

  // hp
  override def reduceHp(amount: Int): Player = copy(hpValue = Math.max(hpValue - amount, 0))

  override def increaseHp(amount: Int): Player = copy(hpValue = Math.min(hpValue + amount, maxHpValue))

  override def setHpValue(amount: Int): Player = copy(hpValue = amount)

  override def isHpEmpty: Boolean = hpValue <= 0

  // mana
  override def reduceMana(amount: Int): Player = copy(manaValue = Math.max(manaValue - amount, 0))

  override def increaseMana(amount: Int): Player = copy(manaValue = Math.min(manaValue + amount, maxManaValue))

  override def resetAndIncreaseMana(): Player = copy(manaValue = maxManaValue + 1, maxManaValue = maxManaValue + 1)

  override def isManaEmpty: Boolean = manaValue <= 0

  override def setManaValue(amount: Int): Player = copy(manaValue = amount)

  // matrix
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
        hpValue / maxHpValue).asInstanceOf[Float].floor.asInstanceOf[Int], "-" *
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