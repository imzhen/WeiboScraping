/**
  * Created by Elliott on 1/11/17.
  */

package weibo

import akka.actor._

class ScraperManager extends Actor with ActorLogging {

  val queue: collection.mutable.Queue[String] = collection.mutable.Queue.empty[String]
  var scrapers: ActorRef = _
  var scraped: List[String] = List.empty[String]

  def receive = ???
}

object ScraperManager {

}
