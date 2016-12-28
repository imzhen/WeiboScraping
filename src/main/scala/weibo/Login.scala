package weibo

//import org.jsoup.Jsoup
//import scala.collection.JavaConverters._
import scalaj.http.Base64
import akka.actor._
import spray.http._
import spray.client.pipelining._
import scala.concurrent._
//import spray.json._
//
//case class WeiboJson (entry: String, gateway: String, from: String, savestate: String,
//                      useticket: String, pagerefer: String, vsnf: String, su: String,
//                      service: String, sp: String, sr: String, encoding: String,
//                      cdult: String, domain: String, prelt: String, returntype: String)
//object WeiboJsonProtocol extends DefaultJsonProtocol {
//  implicit val weiboFormat: RootJsonFormat[WeiboJson] = jsonFormat16(WeiboJson)
//}

class Login (val userName: String, val password: String) {
  def login(): Unit = {
    val userNameDecoded: String = Base64.decodeString(Base64.encodeString(userName))
//    val postData = WeiboJson("sso", "1", "null", "30", "0", "", "1", userNameDecoded,
//                             "sso", password, "1440*900", "UTF-8", "3", "sina.com.cn", "0", "TEXT")
    implicit val postData: String =
      s"""{
      "entry": "sso", "gateway": "1", "from": "null", "savestate": "30",
      "useticket": "0", "pagerefer": "", "vsnf": "1", "su": userNameDecoded,
      "service": "sso", "sp": password, "sr": "1440*900", "encoding": "UTF-8",
      "cdult": "3", "domain": "sina.com.cn", "prelt": "0", "returntype": "TEXT"
      }"""

    val loginEntranceURL: String = "https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.15)"

    implicit val actorSystem = ActorSystem()
    import actorSystem.dispatcher
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive ~> unmarshal[HttpResponse]

    pipeline(Post(loginEntranceURL, postData)).map { response =>
      if (response.status.isFailure) {
        sys.error(s"Received unexpected status ${response.status} : ${response.entity.asString(HttpCharsets.`UTF-8`)}")
      }
      println(s"OK, received ${response.entity.asString(HttpCharsets.`UTF-8`)}")
      println(s"The response header Content-Length was ${response.header[HttpHeaders.`Content-Length`]}")
    }
  }
}

object Main extends App {
  val tester: Login = new Login("imzhenr@gmail.com", "910808pps")
  tester.login()
}
