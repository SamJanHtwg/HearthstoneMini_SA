package core.util

import play.api.libs.json.*
import scala.util.Random
import model.cardComponent.cardImpl.Card

class CardProvider(inputFile: String) {
  private val json: JsValue =
    Json.parse(getClass.getResourceAsStream(inputFile))
  private val allCards = json.as[List[Option[Card]]].filter(_.isDefined)
  // val deck = allCards.filter(_.isInstanceOf[Card]).take(30)
  def getCards(amount: Int): List[Card] =
    Random.shuffle(allCards).take(amount).map((card: Option[Card]) => card.get)

}
