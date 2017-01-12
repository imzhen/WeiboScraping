package weibo

import scala.util.{Failure, Success}
import scalaj.http.Base64
import akka.actor._

import scala.concurrent._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.util.ByteString
//import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.headers.{Cookie, RawHeader}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
//import akka.http.scaladsl.server.Directives
//import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json._

object Utilities {

  def ccToMap(cc: AnyRef): Map[String, String] =
    (Map[String, String]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc).toString)
    }

  //  implicit val weiboJsonMarshaller = Marshaller.opaque[WeiboJson, List[HttpHeader]]{ccToMap(_)}
}

class Login(val userName: String, val password: String) {

  import Utilities._
  import Weibo._

  def login(): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val userNameDecoded: String = Base64.encodeString(userName)
    val postData = WeiboPost("sso", "1", "null", "30", "0", "", "1", userNameDecoded,
      "sso", password, "1440*900", "UTF-8", "3", "sina.com.cn", "0", "TEXT")

    val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
      Http().outgoingConnectionHttps("login.sina.com.cn")

    //    Deprecated because I do not know how to use it in a elegant way...
    //    val request: Future[HttpRequest] = Marshal(postData).to[RequestEntity] map { entity =>
    //      HttpRequest(
    //        uri = Uri("/sso/login.php?client=ssologin.js(v1.4.15)"),
    //        method = HttpMethods.POST,
    //        entity = entity)
    //    }
    //    val responseFuture: Future[HttpResponse] = request flatMap {e =>
    //      Source.single(e)
    //        .via(connectionFlow)
    //        .runWith(Sink.head)
    //    }

    val request: HttpRequest = HttpRequest(
      uri = Uri("/sso/login.php?client=ssologin.js(v1.4.15)"),
      method = HttpMethods.POST,
      entity = FormData(ccToMap(postData)).toEntity)
    val responseFuture: Future[HttpResponse] =
      Source.single(request)
        .via(connectionFlow)
        .runWith(Sink.head)

    responseFuture onComplete {
      case Success(e) =>
        val texts = e.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
        texts map {_.utf8String.parseJson} foreach println
      case Failure(_) => println("request failed")
    }

    system.terminate()

//    responseFuture.andThen {
////      case Success(e) => println(cookieRetrieve(e))
//      case Success(e) =>
//        val texts = e.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
//        texts map {_.utf8String.parseJson} foreach println
////        val tests_json = texts.map {_.utf8String}
////        println(tests_json)
//      case Failure(_) => println("request failed")
//    }.andThen {
//      case _ => system.terminate()
//    }
  }

  def cookieRetrieve(response: HttpResponse): String = {
    val cookies: Seq[HttpCookie] = response.headers.collect {
      case c: `Set-Cookie` => c.cookie
    }
    cookies map (c => List(c.name, c.value).mkString("=")) mkString "; "
  }
}

