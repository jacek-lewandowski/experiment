package net.enigma

import java.util.concurrent.CopyOnWriteArrayList

import scala.collection.JavaConversions._

/**
 * @author Jacek Lewandowski
 */
trait ValueChangedListenable[T] {
  private val listeners = new CopyOnWriteArrayList[T ⇒ Unit]()

  def addValueChangedListener(listener: T ⇒ Unit): this.type = {
    listeners.add(listener)
    this
  }

  def removeValueChangedListener(listener: T ⇒ Unit): this.type = {
    listeners.remove(listener)
    this
  }

  def removeAllValueChangedListeners(): this.type = {
    listeners.clear()
    this
  }

  def notifyListeners(value: T): Unit = {
    for (listener ← listeners) listener(value)
  }
}
