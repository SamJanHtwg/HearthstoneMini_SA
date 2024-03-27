package hearthstoneMini
package model.playerComponent.playerImpl

import scala.collection.immutable.Vector
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.{Field, FieldObject}
import model.playerComponent.PlayerInterface
import model.fieldComponent.fieldImpl.{Field, FieldObject}
import model.fieldComponent.FieldInterface
import model.matrixComponent.matrixImpl.Matrix
import play.api.libs.json.*
import aview.Strings
import hearthstoneMini.model.cardComponent.CardInterface

import java.awt.MenuBar
import scala.xml.Node
import hearthstoneMini.util.CardProvider
import hearthstoneMini.model.cardComponent.cardImpl.Card

/** TODO:
  *   - Serialisierbarkeit Mana
  *   - Serialisierbarkeit Hp
  *   - Check ob drawCard() logik passt
  *   - karten zum friedhof hinzufügen sollte kein optional bekommen
  *   - renderEvenId() und renderUnevenId() fix field
  */

object Player {
  def fromJson(json: JsValue): Player = Player(
    name = (json \\ "name").head.toString.replace("\"", ""),
    hpValue = 0,
    maxHpValue = 0,
    id = (json \\ "id").head.toString().toInt,
    manaValue = 0,
    field = (json \\ "fieldbar").map(card => Card.fromJSON(card)).toVector
  )

  def fromXML(node: Node): Player = Player(
    name = (node \\ "name").head.text,
    hpValue = 0,
    maxHpValue = 0,
    id = (node \\ "id").head.text.toInt,
    manaValue = 0,
    field = (node \\ "field").map(card => Card.fromXML(card)).toVector
  )
}

case class Player(
    name: String = "Player",
    id: Int,
    hpValue: Int = 5,
    maxHpValue: Int = 1,
    maxManaValue: Int = 1,
    manaValue: Int = 1,
    hand: List[CardInterface] =
      new CardProvider("/json/cards.json").getCards(5),
    deck: List[CardInterface] =
      new CardProvider("/json/cards.json").getCards(30),
    friedhof: Array[CardInterface] = Array[CardInterface](),
    field: Vector[Option[CardInterface]] = Vector.tabulate(5) { field => None },

) extends PlayerInterface {

  // player
  override def placeCard(handSlot: Int, fieldSlot: Int): Player = copy(
    field = field.updated(fieldSlot, Some(hand(handSlot))),
    hand = removeCardFromHand(handSlot)
  )

  private def removeCardFromHand(index: Int): List[CardInterface] =
    hand.filter(_ != hand(index))

  override def drawCard(): Player =
    copy(hand = hand.appended(deck(0)), deck = deck.slice(1, deck.length))

  override def reduceAttackCount(slotNum: Int): Player =
    copy(field = field.updated(slotNum, field(slotNum).map(_.reduceAttackCount())))

  override def resetAttackCount(): Player =
    copy(field = field.map(card => card.map(_.resetAttackCount())))

  override def destroyCard(fieldSlot: Int): Player = copy(
    field = field.updated(fieldSlot, None),
    friedhof = friedhof.appended(field(fieldSlot).get)
  )

  override def setName(name: String): Player = copy(name = name)

  // hp
  override def reduceHp(amount: Int): Player =
    copy(hpValue = Math.max(hpValue - amount, 0))

  override def increaseHp(amount: Int): Player =
    copy(hpValue = Math.min(hpValue + amount, maxHpValue))

  override def setHpValue(amount: Int): Player =
    copy(hpValue = amount, maxHpValue = amount)

  override def isHpEmpty: Boolean = hpValue <= 0

  // mana
  override def reduceMana(amount: Int): Player =
    copy(manaValue = Math.max(manaValue - amount, 0))

  override def increaseMana(amount: Int): Player =
    copy(manaValue = Math.min(manaValue + amount, maxManaValue))

  override def resetAndIncreaseMana(): Player =
    copy(manaValue = maxManaValue + 1, maxManaValue = maxManaValue + 1)

  override def isManaEmpty: Boolean = manaValue <= 0

  override def setManaValue(amount: Int): Player =
    copy(manaValue = amount, maxManaValue = amount)

  // matrix
  override def toMatrix: Matrix[String] =
    if (id % 2) == 1 then renderUnevenId() else renderEvenId()

  override def renderUnevenId(): Matrix[String] = new Matrix[String](
    FieldObject.standartMenueBarHeight + FieldObject.standartGameBarHeight + FieldObject.standartFieldBarHeight,
    FieldObject.standartFieldWidth,
    " "
  )
    .updateMatrixWithMatrix(0, 0, menueBar())

    // .updateMatrixWithMatrix(
    //   FieldObject.standartGameBarHeight + FieldObject.standartMenueBarHeight,
    //   0,
    //   field.toMatrix
    // )

  override def renderEvenId(): Matrix[String] = new Matrix[String](
    FieldObject.standartMenueBarHeight + FieldObject.standartGameBarHeight + FieldObject.standartFieldBarHeight,
    FieldObject.standartFieldWidth,
    " "
  )
    // .updateMatrixWithMatrix(0, 0, fieldbar.toMatrix)
    .updateMatrixWithMatrix(
      FieldObject.standartFieldBarHeight + FieldObject.standartGameBarHeight,
      0,
      menueBar()
    )

  override def reduceDefVal(slotNum: Int, amount: Int): Player = copy(
    field = field.updated(slotNum, field(slotNum).map(_.reduceHP(amount)))
  )

  override def menueBar(): Matrix[String] = new Matrix[String](
    FieldObject.standartMenueBarHeight,
    FieldObject.standartFieldWidth,
    " "
  )
    .updateMatrix(
      0,
      0,
      List[String](
        name + " " +
          "#" * ((FieldObject.standartFieldWidth - name.length - 1) *
            hpValue / maxHpValue).asInstanceOf[Float].floor.asInstanceOf[Int],
        "-" *
          FieldObject.standartFieldWidth
      )
    )

  override def toJson: JsValue = Json.obj(
    "name" -> name,
    "id" -> id,
    // "field" -> field.toJson
  )

  override def toXML: Node =
    <Player>
      <name>
        {name}
      </name>
      <id>
        // {id.toString}
      </id>
      <field>
        // {field}
      </field>
    </Player>
}
