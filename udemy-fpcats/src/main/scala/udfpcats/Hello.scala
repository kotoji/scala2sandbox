package example

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success}

object HttpTextClient {
  def get(url: String): BufferedSource = Source.fromURL(url)
}

object Hello extends App {

  def getAsync(url: String): Future[String] =
    Future(HttpTextClient.get(url)).map(s =>
      try s.mkString
      finally s.close()
    )

  def extractUrlAsync(body: String): Future[Seq[String]] = {
    val urlRegix = """https?://[\w.:$%?&()#\-=+~]+""".r
    Future {
      urlRegix.findAllIn(body).toSeq
    }
  }

  val url = "http://www2.nagano.ac.jp/hiraoka/BP/14_1.html"

  val urlsFuture: Future[Seq[String]] =
    getAsync(url).flatMap(extractUrlAsync)

  urlsFuture
    .onComplete {
      case Success(urlList) => println(urlList.mkString("\n"))
      case Failure(t)       => println(t.getMessage)
    }

  Await.ready(urlsFuture, Duration.Inf)
}
