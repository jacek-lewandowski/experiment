package net.enigma.model

import net.enigma.App

/**
 * @author Jacek Lewandowski
 */
case class ShuffleRange(start: Int, end: Int) {
  def shuffle[T](collection: List[T]): List[T] = {
    if ((collection.size - start) < 2) {
      collection
    } else {
      val realEnd = end min collection.size
      collection.patch(start, App.random.shuffle(collection.slice(start, realEnd)), realEnd - start)
    }
  }
}


