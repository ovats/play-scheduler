package tasks

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.inject.{SimpleModule, bind}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps


class BackgroundTaskWithFixedDelay extends SimpleModule(bind[SetTaskToRunWithFixedDelay].toSelf.eagerly())

class SetTaskToRunWithFixedDelay  @Inject()(actorSystem: ActorSystem)  (
  implicit executionContext: ExecutionContext
) {

  actorSystem.log.info("[SetTaskToRunWithFixedDelay]")
  actorSystem.log.info("[SetTaskToRunWithFixedDelay] " + new java.util.Date())

  actorSystem.scheduler
    .scheduleWithFixedDelay(initialDelay = 2 seconds, delay = 3 seconds)(
      new TaskWithFixedDelayRunnable(actorSystem)
    )

}

class TaskWithFixedDelayRunnable @Inject()(actorSystem: ActorSystem)
  extends Runnable {
  override def run(): Unit = {
    actorSystem.log.info("[TaskWithFixedDelayRunnable] Executing something...")
    actorSystem.log.info("[TaskWithFixedDelayRunnable] Start: " + new java.util.Date())
    Thread.sleep(1000)
    actorSystem.log.info("[TaskWithFixedDelayRunnable] End: " + new java.util.Date())
  }
}


2020-07-11 21:44:30 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [SetTaskToRunWithFixedDelay]
2020-07-11 21:44:30 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [SetTaskToRunWithFixedDelay] Sat Jul 11 21:44:30 UYT 2020

2020-07-11 21:44:32 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] Executing something...
2020-07-11 21:44:32 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] Start: Sat Jul 11 21:44:32 UYT 2020
2020-07-11 21:44:33 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] End: Sat Jul 11 21:44:33 UYT 2020
2020-07-11 21:44:36 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] Executing something...
2020-07-11 21:44:36 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] Start: Sat Jul 11 21:44:36 UYT 2020
2020-07-11 21:44:37 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] End: Sat Jul 11 21:44:37 UYT 2020
2020-07-11 21:44:40 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] Executing something...
2020-07-11 21:44:40 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] Start: Sat Jul 11 21:44:40 UYT 2020
2020-07-11 21:44:41 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskWithFixedDelayRunnable] End: Sat Jul 11 21:44:41 UYT 2020
