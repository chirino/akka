package se.scalablesolutions.akka.kernel.actor

import concurrent.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.TimeUnit
import reactor._

import org.junit.{Test, Before}
import org.junit.Assert._

class ActorTest {
  private val unit = TimeUnit.MILLISECONDS

  class TestActor extends Actor {
    def receive: PartialFunction[Any, Unit] = {
      case "Hello" =>
        reply("World")
      case "Failure" =>
        throw new RuntimeException("expected")
    }
  }
  
  @Test
  def sendOneWay = {
    implicit val timeout = 5000L
    var oneWay = "nada"
    val actor = new Actor {
      def receive: PartialFunction[Any, Unit] = {
        case "OneWay" => oneWay = "received"
      }
    }
    actor.start
    val result = actor ! "OneWay"
    Thread.sleep(100)
    assertEquals("received", oneWay)
    actor.stop
  }

  @Test
  def sendReplySync = {
    implicit val timeout = 5000L
    val actor = new TestActor
    actor.start
    val result: String = actor !? "Hello"
    assertEquals("World", result)
    actor.stop
  }

  @Test
  def sendReplyAsync = {
    implicit val timeout = 5000L
    val actor = new TestActor
    actor.start
    val result = actor !! "Hello"
    assertEquals("World", result.get.asInstanceOf[String])
    actor.stop
  }

  @Test
  def sendReceiveException = {
    implicit val timeout = 5000L
    val actor = new TestActor
    actor.start
    try {
      actor !! "Failure"
      fail("Should have thrown an exception")
    } catch {
      case e =>
        assertEquals("expected", e.getMessage())
    }
    actor.stop
  }
}