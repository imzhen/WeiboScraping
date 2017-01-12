package weibo

/**
  * Created by Elliott on 1/12/17.
  */

object Utility {
  def ccToMap(cc: AnyRef): Map[String, String] =
    (Map[String, String]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc).toString)
    }

  //    implicit val weiboJsonMarshaller = Marshaller.opaque[Weibo.WeiboPost, List[HttpHeader]]{ccToMap(_)}
}
