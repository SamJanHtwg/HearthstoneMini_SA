package model

object GameState extends Enumeration {
  type GameState = Value
  val CHOOSEMODE, ENTERPLAYERNAMES, EXIT, MAINGAME, WIN = Value
}
