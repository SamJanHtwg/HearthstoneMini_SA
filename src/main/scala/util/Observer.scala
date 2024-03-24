package hearthstoneMini
package util

trait Observer:
  def update(e: Event, msg: Option[String]): Unit

trait Observable:
  private var subscribers: Vector[Observer] = Vector()
  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def notifyObservers(e: Event, msg: Option[String]): Unit = subscribers.foreach(o => o.update(e, msg))

enum Event:
  case EXIT
  case PLAY
  case ERROR
