package hearthstoneMini
package util

trait Observer:
  def update(e: Event, msg: Option[String]): Unit

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  def notifyObservers(e: Event, msg: Option[String]) = subscribers.foreach(o => o.update(e, msg))

enum Event:
  case EXIT
  case PLAY
  case ERROR
