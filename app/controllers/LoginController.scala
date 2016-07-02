package controllers

import codecraft.auth._
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
class LoginController @Inject() (cloud: ICloud) extends Controller {
  implicit val formatGetAuth = Json.format[GetAuth]
  implicit val formatGetAuthReply = Json.format[GetAuthReply]

  def login = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[GetAuth].fold(
      errors => Future {
        BadRequest(JsError toJson errors)
      },
      getAuth => {
        Logger.info(s"Logging in user $getAuth")
        cloud.requestCmd("auth.get", getAuth, 5 seconds).mapTo[GetAuthReply] map { reply =>
          Ok(Json toJson reply)
        }
      }
    )
  }
}
