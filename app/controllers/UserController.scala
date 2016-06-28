package controllers

import codecraft.platform.ICloud
import codecraft.user._
import javax.inject._
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.duration._
import scala.concurrent.Future

@Singleton
class UserController @Inject() (cloud: ICloud) extends Controller {
  implicit val formatUser = Json.format[User]
  implicit val formatAddUser = Json.format[AddUser]
  implicit val formatAddUserReply = Json.format[AddUserReply]
  implicit val formatGetUser = Json.format[GetUser]
  implicit val formatGetUserReply = Json.format[GetUserReply]

  def add = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[AddUser].fold(
      errors => {
        Future { BadRequest(JsError.toJson(errors)) }
      },
      addUser => {
        cloud.requestCmd("user.add", addUser, 5 seconds).mapTo[AddUserReply] map { reply =>
          Ok(Json.toJson(reply))
        }
      }
    )
  }

  def get(id: String) = Action.async {
    println("Got it!")
    val cmd = GetUser(id)
    cloud.requestCmd("user.get", cmd, 5 seconds).mapTo[GetUserReply] map { reply =>
      Ok(Json.toJson(reply))
    }
  }
}

