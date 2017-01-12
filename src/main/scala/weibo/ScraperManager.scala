/**
  * Created by Elliott on 1/11/17.
  */

package weibo

import akka.actor._

class ScraperManager extends Actor with ActorLogging {

  var scrapers: ActorRef = _
  val queue: collection.mutable.Queue[String] = collection.mutable.Queue.empty[String]
  var scraped: List[String] = List.empty[String]

  def receive = ???
}

object ScraperManager {

}
