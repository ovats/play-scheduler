# play-scheduler

This project demonstrates three different ways to trigger background tasks using Play framework.
Dependencies and plugins:

- Scala 2.13.3
- Play Framework 2.8.2
- SBT 1.3.13

## Three different ways

We have to choose three flavors:

- scheduleOnce
- scheduleAtFixedRate
- scheduleWithFixedDelay

### scheduleOnce

This is the simplest one, and you know the result just reading its name: the task will be run only once.
The task will be executed only once, and you can define some delay to wait before executing the task.

### scheduleAtFixedRate & scheduleWithFixedDelay

Both look similar so what's the difference? Let's see some examples.

Method `scheduleAtFixedRate` takes this two parameters (among others):

- initialDelay
- interval

Let's say your Play application starts at 10:00 am, and you set `intialDelay` to 2 seconds and `interval` to 3 seconds.
Also your task takes 1 second to complete. Then you will have something like this:

```
10:00 Play application starts
10:02 start executing your task for the first time
10:03 your task completes
10:05 start executing your task for the second time (10:02 + 3 seconds)
10:06 your task completes again
...
```

Method `scheduleWithFixedDelay` takes similar parameters (among others):

- initialDelay
- delay

Again let's say your Play application starts at 10:00 am, and you set `intialDelay` to 2 seconds and `interval` to 3 seconds:

```
10:00 Play application starts
10:02 start executing your task for the first time
10:03 your task completes
10:06 start executing your task for the second time (10:03 + 3 seconds)
10:07 your task completes again
...
```

In this case the next time to execute will be computed using the date and time when task completes.

## Implementation

### scheduleAtFixedRate

First we need to tell to Play to execute something when it starts. To do that we add this to `application.conf`:

`play.modules.enabled += "tasks.BackgroundTaskAtFixedRate"`

It means in package `tasks` class `BackgroundTaskAtFixedRate` will be executed at start.

We need to define the class like this:

`class BackgroundTaskAtFixedRate extends SimpleModule(bind[SetTaskToRunAtFixedRate].toSelf.eagerly())`

The class `SetTaskToRunAtFixedRate` will contain the code we want to run, in this case we are going to use Akka scheduler included in Play:

```scala
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
```

Here we are passing to method `scheduleAtFixedRate` 2 seconds as initial delay and 3 seconds as interval for each execution.
Also the task is passed as an instance of class `TaskAtFixedRateRunnable`.

```scala
class TaskAtFixedRateRunnable @Inject()(actorSystem: ActorSystem)
     extends Runnable {
     override def run(): Unit = {
       actorSystem.log.info("[TaskAtFixedRateRunnable] Executing something...")
       actorSystem.log.info("[TaskAtFixedRateRunnable] Start: " + new java.util.Date())
       Thread.sleep(1000)
       actorSystem.log.info("[TaskAtFixedRateRunnable] End: " + new java.util.Date())
     }
   }
```

We are not doing nothing special, just loggin message to show the use of the scheduler.

If you run the application with `sbt run` you will:

```
2020-07-11 20:49:19 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [SetTaskToRunAtFixedRate]
2020-07-11 20:49:19 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [SetTaskToRunAtFixedRate] Sat Jul 11 20:49:19 UYT 2020
2020-07-11 20:49:21 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Executing something...
2020-07-11 20:49:21 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Start: Sat Jul 11 20:49:21 UYT 2020
2020-07-11 20:49:22 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] End: Sat Jul 11 20:49:22 UYT 2020
2020-07-11 20:49:24 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Executing something...
2020-07-11 20:49:24 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Start: Sat Jul 11 20:49:24 UYT 2020
2020-07-11 20:49:25 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] End: Sat Jul 11 20:49:25 UYT 2020
2020-07-11 20:49:27 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Executing something...
2020-07-11 20:49:27 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Start: Sat Jul 11 20:49:27 UYT 2020
2020-07-11 20:49:28 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] End: Sat Jul 11 20:49:28 UYT 2020
2020-07-11 20:49:30 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Executing something...
2020-07-11 20:49:30 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Start: Sat Jul 11 20:49:30 UYT 2020
2020-07-11 20:49:31 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] End: Sat Jul 11 20:49:31 UYT 2020
2020-07-11 20:49:33 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Executing something...
2020-07-11 20:49:33 INFO  akka.actor.ActorSystemImpl akka.actor.ActorSystemImpl(application) [TaskAtFixedRateRunnable] Start: Sat Jul 11 20:49:33 UYT 2020
...
```

First two lines shows class `SetTaskToRunAtFixedRate` invoking method `scheduleAtFixedRate`.
Then every 3 seconds the app logs the task being executed.
 
### scheduleWithFixedDelay

This is very similar to method `scheduleAtFixedRate`:

- Add an entry in `application.conf` pointing to `tasks.BackgroundTaskWithFixedDelay`.
- Create the class `BackgroundTaskWithFixedDelay`.
- Create a new class `SetTaskToRunWithFixedDelay` which will be invoked by `BackgroundTaskWithFixedDelay`.
- This class will set the scheduler invoking method `scheduleWithFixedDelay`.
- The scheduler will execute the task of a new class in `TaskWithFixedDelayRunnable`.

After running the application the output is:

```
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
```

### scheduleOnce

Finally, we added a new feature in the app that will trigger a task but it will only run once.
The steps are the same:

- Add an entry in `application.conf` pointing to `tasks.BackgroundTaskOnce`.
- Create the class `BackgroundTaskOnce`.
- Create a new class `SetTaskToRunOnce` which will be invoked by `BackgroundTaskOnce`.
- This class will set the scheduler invoking method `scheduleOnce`.
- The scheduler will execute the task of a new class in `TaskToRunOnce`.
