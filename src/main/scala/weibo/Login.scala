package weibo

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.stream.scaladsl._
import spray.json._

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scalaj.http.Base64


object Login {

  case class CookieString(cookie: String)

  case object LoggingIn

}

class Login(val userName: String, val password: String) extends Actor with ActorLogging {

  import Login._

  def receive = {
    case LoggingIn => login()
  }

  def login(): Unit = {

    implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))
    implicit val executionContext = context.dispatcher

    val userNameDecoded: String = Base64.encodeString(userName)
    val postData = Weibo.WeiboPost("sso", "1", "null", "30", "0", "", "1", userNameDecoded,
      "sso", password, "1440*900", "UTF-8", "3", "sina.com.cn", "0", "TEXT")

    val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
      Http(context.system).outgoingConnectionHttps("login.sina.com.cn")
    val request: HttpRequest = HttpRequest(
      uri = Uri("/sso/login.php?client=ssologin.js(v1.4.15)"),
      method = HttpMethods.POST,
      entity = FormData(Utility.ccToMap(postData)).toEntity)
    val responseFuture: Future[HttpResponse] =
      Source.single(request)
        .via(connectionFlow)
        .runWith(Sink.head)

    responseFuture.andThen {
      case Success(e) =>
        val texts: Future[String] = Unmarshal(e.entity).to[String]
        val texts_json: JsObject = Await.result(texts map {
          _.parseJson
        }, 5.seconds).asInstanceOf[JsObject]
        texts_json.fields("retcode") match {
          case JsString("0") => println(CookieString(cookieRetrieve(e)))
          case _ => println("login failed")
        }
      case Failure(_) => println("request failed")
    }.andThen {
      case _ => self ! PoisonPill
      //        Http().shutdownAllConnectionPools() andThen { case _ => system.terminate() }
    }
  }

  def cookieRetrieve(response: HttpResponse): String = {
    val cookies: Seq[HttpCookie] = response.headers.collect {
      case c: `Set-Cookie` => c.cookie
    }
    cookies map (c => List(c.name, c.value).mkString("=")) mkString ";"
  }
}

