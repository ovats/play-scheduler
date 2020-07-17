package tasks

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.inject.{SimpleModule, bind}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.language.postfixOps

class BackgroundTaskOnce extends SimpleModule(bind[SetTaskToRunOnce].toSelf.eagerly()) 

class SetTaskToRunOnce  @Inject()(actorSystem: ActorSystem)  (
  implicit executionContext: ExecutionContext
) {

  actorSystem.log.info("[SetTaskToRunOnce]")
  actorSystem.log.info("[SetTaskToRunOnce] " + new java.util.Date())

  actorSystem.scheduler
    .scheduleOnce(delay = 3 seconds)(
      new TaskToRunOnce(actorSystem).someTask()
    )

}

class TaskToRunOnce @Inject()(actorSystem: ActorSystem) {

  def someTask(): Unit = {
    actorSystem.log.info("[TaskToRunOnce] Executing something...")
    actorSystem.log.info("[TaskToRunOnce] Start: " + new java.util.Date())
    Thread.sleep(1000)
    actorSystem.log.info("[TaskToRunOnce] End: " + new java.util.Date())
  }

}