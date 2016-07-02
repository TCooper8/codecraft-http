import com.google.inject.AbstractModule
import java.time.Clock

import akka.actor.ActorSystem
import codecraft.user._
import codecraft.auth._
import codecraft.platform.amqp._
import codecraft.platform.ICloud
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {
  val config = ConfigFactory.load()

	// Generate the routing information for user commands.
	val routingInfo = RoutingInfo(
    List(
      UserRoutingGroup.cmdInfo.map {
        case registry => (registry.key, registry)
      }.toMap,
      AuthRoutingGroup.cmdInfo.map {
        case registry => (registry.key, registry)
      }.toMap
    ).foldLeft(Map.empty[String, codecraft.codegen.CmdRegistry]) {
      case (acc, a) => acc ++ a
    },
    Map(
      UserRoutingGroup.groupRouting.queueName -> UserRoutingGroup.groupRouting,
      AuthRoutingGroup.groupRouting.queueName -> AuthRoutingGroup.groupRouting
    )
  )

  val system = ActorSystem("api")
  println("Creating cloud")
  val cloud = AmqpCloud(
    system,
    config.getStringList("amqp.hosts").toList,
    routingInfo
  ).asInstanceOf[ICloud]
  println("Cloud created")

  override def configure() = {
    println(s"Binding services...")
    bind(classOf[ICloud]).toInstance(cloud)
    println(s"Bound services")
  }
}
