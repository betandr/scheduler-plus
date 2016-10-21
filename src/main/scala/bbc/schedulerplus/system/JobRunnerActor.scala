/*
 * Copyright (c) 2016 BBC Design and Engineering
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bbc.schedulerplus.system

import akka.actor.Actor
import akka.event.Logging
import bbc.AppContext
import bbc.schedulerplus.Job

import scala.concurrent.duration._

/**
  * Akka Scheduler Actor which will execute the anonymous function 'callback()' after the job.lifetimeInMills has elapsed
  */
class JobRunnerActor(job: Job, callback: () => Unit) extends Actor {
  import context.dispatcher

  lazy val tick = context.system.scheduler.scheduleOnce(
    job.lifetimeInMillis milliseconds,
    self,
    "run-job"
  )

  val log = Logging(AppContext.akkaSystem, getClass)

  // scalastyle:off
  override def postStop() = tick.cancel()
  // scalastyle:on

  // scalastyle:off
  def receive = {
    // scalastyle:on
    case "run-job" => {
      log.debug("Running job runner for " + job.toKey + "  ...")

      callback()
    }
  }
}
