package controllers

import codecraft.music._
import codecraft.platform.ICloud
import javax.inject._
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

@Singleton
class MusicController @Inject() (cloud: ICloud) extends Controller {
  implicit val formatSongRecord = Json.format[SongRecord]
  implicit val formatAddSong = Json.format[AddSong]
  implicit val formatAddSongReply = Json.format[AddSongReply]
  implicit val formatGetSong = Json.format[GetSong]
  implicit val formatGetSongReply = Json.format[GetSongReply]

  def add = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[AddSong].fold(
      errors => {
        Future { BadRequest(JsError.toJson(errors)) }
      },
      addSong => {
        cloud.requestCmd("music.add", addSong, 5 seconds).mapTo[AddSongReply] map { reply =>
          Ok(Json.toJson(reply))
        }
      }
    )
  }

  def get(id: String) = Action.async {
    val cmd = GetSong(id, "tmp")
    cloud.requestCmd("music.get", cmd, 5 seconds).mapTo[GetSongReply] map { reply =>
      Ok(Json.toJson(reply))
    }
  }
}

