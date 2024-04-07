package hearthstoneMini
package model.playerComponent.playerImpl

import hearthstoneMini.model.cardComponent.CardInterface
import hearthstoneMini.model.cardComponent.cardImpl.Card
import hearthstoneMini.model.fieldComponent.fieldImpl.FieldObject
import hearthstoneMini.model.matrixComponent.matrixImpl.Matrix
import hearthstoneMini.model.playerComponent.PlayerInterface
import hearthstoneMini.util.CardProvider
import play.api.libs.json.*

import scala.collection.immutable.Vector
import scala.xml.Node
import scala.util.Try

/** TODO:
  *   - karten zum friedhof hinzufügen sollte kein optional bekommen
  *   - renderEvenId() und renderUnevenId() fix field
  */

object Player {
  def fromJson(json: JsValue): Player =
    Player(
      name = (json \ "name").as[String],
      hpValue = (json \ "hpValue").as[Int],
      maxHpValue = (json \ "maxHpValue").as[Int],
      id = (json \ "id").as[Int],
      manaValue = (json \ "manaValue").as[Int],
      maxManaValue = (json \ "maxManaValue").as[Int],
      hand = (json \ "hand").as[List[JsValue]].map(card => Card.fromJson(card)),
      deck = (json \ "deck").as[List[JsValue]].map(card => Card.fromJson(card)),
      friedhof = (json \ "friedhof")
        .as[List[JsValue]]
        .map(card => Card.fromJson(card))
        .toArray,
      field = (json \ "field")
        .as[List[JsValue]]
        .map(card => Try(Card.fromJson(card)).toOption)
        .toVector
    )

  def fromXml(node: Node): Player = Player(
    name = (node \ "name").text.trim,
    hpValue = (node \ "hpValue").text.trim.toInt,
    maxHpValue = (node \ "maxHpValue").text.trim.toInt,
    id = (node \ "id").text.trim.toInt,
    manaValue = (node \ "manaValue").text.trim.toInt,
    maxManaValue = (node \ "maxManaValue").text.trim.toInt,
    hand = (node \ "hand")
      .map(card => card \ "card")
      .flatten
      .map(card => Card.fromXml(card))
      .toList,
    deck = (node \ "deck")
      .map(card => card \ "card")
      .flatten
      .map(card => Card.fromXml(card))
      .toList,
    friedhof = (node \ "friedhof")
      .map(card => card \ "card")
      .flatten
      .map(card => Card.fromXml(card))
      .toArray,
    field = (node \ "field")
      .map(card => card \ "card")
      .flatten
      .map(card => Try(Card.fromXml(card)).toOption)
      .toList
      .toVector
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
    field: Vector[Option[CardInterface]] = Vector.tabulate(5) { field => None }
) extends PlayerInterface {

  private enum ValueType {
    case HP, MANA
  }

  private def updateValue(valueType: ValueType): Int => Player = {
    (amount: Int) =>
      {
        val updatedValue = valueType match {
          case ValueType.HP =>
            Math.max(Math.min(hpValue + amount, maxHpValue), 0)
          case ValueType.MANA =>
            Math.min(Math.max(manaValue + amount, 0), maxManaValue)
        }
        valueType match {
          case ValueType.HP   => copy(hpValue = updatedValue)
          case ValueType.MANA => copy(manaValue = updatedValue)
        }
      }
  }

  // player
  override def placeCard(handSlot: Int, fieldSlot: Int): Player = copy(
    field = field.updated(fieldSlot, Some(hand(handSlot))),
    hand = removeCardFromHand(handSlot)
  )

  private def removeCardFromHand(index: Int): List[CardInterface] =
    hand.filter(_ != hand(index))

  override def drawCard(): Player =
    copy(hand = hand.appended(deck.head), deck = deck.slice(1, deck.length))

  override def reduceAttackCount(slotNum: Int): Player =
    copy(field =
      field.updated(slotNum, field(slotNum).map(_.reduceAttackCount()))
    )

  override def resetAttackCount(): Player =
    copy(field = field.map(card => card.map(_.resetAttackCount())))

  override def destroyCard(fieldSlot: Int): Player = copy(
    field = field.updated(fieldSlot, None),
    friedhof = friedhof.appended(field(fieldSlot).get)
  )

  override def setName(name: String): Player = copy(name = name)

  // HP
  override def reduceHp(amount: Int): Player =
    updateValue(ValueType.HP)(-amount)

  override def increaseHp(amount: Int): Player =
    updateValue(ValueType.HP)(amount)

  override def setHpValue(amount: Int): Player =
    copy(hpValue = amount, maxHpValue = amount)

  override def isHpEmpty: Boolean = hpValue <= 0

  // Mana
  override def reduceMana(amount: Int): Player =
    updateValue(ValueType.MANA)(-amount)

  override def increaseMana(amount: Int): Player =
    updateValue(ValueType.MANA)(amount)

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
    "hand" -> hand.map(_.toJson),
    "deck" -> deck.map(_.toJson),
    "friedhof" -> friedhof.map(_.toJson),
    "hpValue" -> Json.toJson(hpValue),
    "maxHpValue" -> Json.toJson(maxHpValue),
    "manaValue" -> Json.toJson(manaValue),
    "maxManaValue" -> Json.toJson(maxManaValue),
    "field" -> field.map(_.map(_.toJson))
  )

  override def toXml: Node =
    <player>
      <name>
        {name}
      </name>
      <id>
        {id}
      </id>
      <hpValue>
        {hpValue}
      </hpValue>
      <maxHpValue>
        {maxHpValue}
      </maxHpValue>
      <manaValue>
        {manaValue}
      </manaValue>
      <maxManaValue>
        {maxManaValue}
      </maxManaValue>
      <hand>
        {hand.map(_.toXML)}
      </hand>
      <deck>
        {deck.map(_.toXML)}
      </deck>
      <friedhof>
        {friedhof.map(_.toXML)}
      </friedhof>
      <field>
        {field.map(_.map(_.toXML).getOrElse(<card> </card>))}
      </field>
    </player>
}
