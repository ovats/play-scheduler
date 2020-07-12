package tasks

import akka.actor.{ActorLogging, ActorSystem}
import javax.inject.Inject
import play.api.inject.{SimpleModule, bind}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

class BackgroundTaskAtFixedRate extends SimpleModule(bind[SetTaskToRunAtFixedRate].toSelf.eagerly())

class SetTaskToRunAtFixedRate @Inject()(actorSystem: ActorSystem)  (
  implicit executionContext: ExecutionContext
) {

  // This will set a task that will be first executed after 2 seconds (parameter initialDelay).
  // and then will be executed every 3 seconds (parameter interval).

  actorSystem.log.info("[SetTaskToRunAtFixedRate]")
  actorSystem.log.info("[SetTaskToRunAtFixedRate] " + new java.util.Date())

  actorSystem.scheduler
    .scheduleAtFixedRate(initialDelay = 2 seconds, interval = 3 seconds)(
      new TaskAtFixedRateRunnable(actorSystem)
    )

}

class TaskAtFixedRateRunnable @Inject()(actorSystem: ActorSystem)
  extends Runnable {
  override def run(): Unit = {
    actorSystem.log.info("[TaskAtFixedRateRunnable] Executing something...")
    actorSystem.log.info("[TaskAtFixedRateRunnable] Start: " + new java.util.Date())
    Thread.sleep(1000)
    actorSystem.log.info("[TaskAtFixedRateRunnable] End: " + new java.util.Date())
  }
}
