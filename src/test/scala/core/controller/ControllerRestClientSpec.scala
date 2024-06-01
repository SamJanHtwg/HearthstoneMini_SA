package hearthstoneMini.controller

import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import core.controller.component.controllerImpl.ControllerRestClient
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.BeforeAndAfterEach
import core.controller.service.HttpService
import scala.util.Success
import play.api.libs.json.JsValue
import spray.json.JsString
import play.api.libs.json.Json
import model.Move
import model.GameState
import scala.util.Failure
import model.fieldComponent.fieldImpl.Field
import model.playerComponent.playerImpl.Player
import core.controller.Strategy

class ControllerRestClientTest
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterEach {
  var mockHttpService: HttpService = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockHttpService = mock(classOf[HttpService])
  }

  "A ControllerRestClient" should {
    "call HttpService for canUndo" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Json.parse("true")))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.canUndo
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for canUndo and return falls on failure" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Failure(new Exception("error")))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      val result = controllerRestClient.canUndo
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
      assert(result == false)
    }
    "call HttpService for canRedo" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Json.parse("true")))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.canRedo
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for canRedo and return false on failure" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Failure(new Exception("error")))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      val result = controllerRestClient.canRedo
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
      assert(result == false)
    }
    "call HttpService for placeCard" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.placeCard(Move())
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for drawCard" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.drawCard()
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for setPlayerNames" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.setPlayerNames("player1", "player2")
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for setGameState" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.setGameState(GameState.CHOOSEMODE)
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for attack" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.attack(Move())
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for directAttack" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.directAttack(Move())
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for switchPlayer" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.switchPlayer()
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for undo" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.undo
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for redo" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.redo
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for exitGame" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.exitGame()
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for saveField" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.saveField
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for loadField" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.loadField
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "call HttpService for setStrategy" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)

      controllerRestClient.setStrategy(Strategy.normal)
      verify(mockHttpService, times(2)).request(
        anyString(),
        anyString(),
        any(),
        any()
      )
    }
    "evaluate winner on getWinner" in {
      when(mockHttpService.request(anyString(), anyString(), any(), any()))
        .thenReturn(Success(Field().toJson))
      val controllerRestClient = new ControllerRestClient(using mockHttpService)
      val result = controllerRestClient.getWinner()
      assert(result == None)

      controllerRestClient.field = Field(players =
        ((1, Player("player1", 1, hpValue = 0)) :: (
          2,
          Player("player2", 2)
        ) :: Nil).toMap
      )
      val result2 = controllerRestClient.getWinner()
      assert(result2 == Some("player2"))
    }
  }
}
