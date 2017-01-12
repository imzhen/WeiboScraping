/**
  * Created by Elliott on 1/11/17.
  */

package weibo

import akka.actor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  val system = ActorSystem("WeiboScraping")
  val login = system.actorOf(Props(new Login("imzhenr@gmail.com", "910808pps")))
  login ! Login.LoggingIn
  system.scheduler.scheduleOnce(5.seconds) {
    system.terminate()
  }
}