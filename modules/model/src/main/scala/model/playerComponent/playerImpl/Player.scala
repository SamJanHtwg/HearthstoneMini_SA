package model
package playerComponent.playerImpl

import cardComponent.CardInterface
import cardComponent.cardImpl.Card
import fieldComponent.fieldImpl.Field
import playerComponent.PlayerInterface
import play.api.libs.json.*

import scala.collection.immutable.Vector
import scala.util.Try

/** TODO:
  *   - karten zum friedhof hinzufügen sollte kein optional bekommen
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
}

case class Player(
    name: String = "Player",
    id: Int,
    hpValue: Int = 1,
    maxHpValue: Int = 5,
    maxManaValue: Int = 2,
    manaValue: Int = 1,
    hand: List[CardInterface] = List[CardInterface](),
    deck: List[CardInterface] = List[CardInterface](),
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

  override def reduceDefVal(slotNum: Int, amount: Int): Player = copy(
    field = field.updated(slotNum, field(slotNum).map(_.reduceHP(amount)))
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
}
