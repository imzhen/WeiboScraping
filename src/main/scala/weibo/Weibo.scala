/**
  * Created by Elliott on 1/11/17.
  */

package weibo

import spray.json._

object Weibo {
  case class WeiboPost(entry: String, gateway: String, from: String, savestate: String,
                       useticket: String, pagerefer: String, vsnf: String, su: String,
                       service: String, sp: String, sr: String, encoding: String,
                       cdult: String, domain: String, prelt: String, returntype: String)
}

object WeiboJsonProtocol extends DefaultJsonProtocol {
  import Weibo._
  implicit val weiboFormat: RootJsonFormat[WeiboPost] = jsonFormat16(WeiboPost)
}