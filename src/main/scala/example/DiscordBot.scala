package example

import cats.effect._
import cats.implicits._
import org.http4s.Uri

import java.net.http.HttpClient
import org.http4s.client.websocket._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.jdkhttpclient._

import scala.language.postfixOps

object DiscordBot extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    import cats.effect.unsafe.implicits.global

    val (http, webSocket) =
      Resource
        .eval(IO(HttpClient.newHttpClient()))
        .flatMap { httpClient =>
          (JdkHttpClient[IO](httpClient), JdkWSClient[IO](httpClient)).tupled
        }
        .allocated
        .map(_._1)
        .unsafeRunSync()

    val uriDiscord = uri"wss://gateway.discord.gg/?v=9&encoding=json"
    val uriPostman = uri"wss://ws.postman-echo.com/raw"

    val resource = createConnection(webSocket, uriPostman)
    val res = resource.use { conn => receive(conn) }

    res.flatMap(IO.println) *> IO.pure(ExitCode.Success)
  }

  def createConnection(
      webSocket: WSClient[IO],
      uri: Uri
  ): Resource[IO, WSConnectionHighLevel[IO]] = {
    webSocket.connectHighLevel(WSRequest(uri))
  }

  def receive(conn: WSConnectionHighLevel[IO]): IO[String] =
    for {
      _ <- conn.send(WSFrame.Text("reality"))
      _ <- conn.sendMany(
        List(
          WSFrame.Text("is often"),
          WSFrame.Text("disappointing.")
        )
      )
      received <- conn.receiveStream
        .collect { case WSFrame.Text(str, _) => str }
        .evalTap(str => conn.send(WSFrame.Text(str.toUpperCase)))
        .take(6)
        .compile
        .toList
    } yield received.mkString(" ")
}
