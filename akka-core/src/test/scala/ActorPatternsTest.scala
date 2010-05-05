package se.scalablesolutions.akka.patterns

import java.util.concurrent.atomic.AtomicInteger
import se.scalablesolutions.akka.config.ScalaConfig._
import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.util.Logging

import org.scalatest.Suite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.junit.{Before, After, Test}
import scala.collection.mutable.HashSet

@RunWith(classOf[JUnitRunner])
class ActorPatternsTest extends junit.framework.TestCase with Suite with MustMatchers with Logging {
  import Patterns._
  @Test def testDispatcher = {
    val (testMsg1,testMsg2,testMsg3,testMsg4) = ("test1","test2","test3","test4")

    var targetOk = 0
    val t1 = actor {
      case `testMsg1` => targetOk += 2
      case `testMsg2` => targetOk += 4
    }

    val t2 = actor {
      case `testMsg3` => targetOk += 8
    }

    val d = dispatcherActor {
      case `testMsg1`|`testMsg2` => t1
      case `testMsg3` => t2
    }.start
      
    d ! testMsg1
    d ! testMsg2
    d ! testMsg3
    Thread.sleep(1000)
    targetOk must be(14)
    t1.stop
    t2.stop
    d.stop
  }

  @Test def testLogger = {
    val msgs = new HashSet[Any]
    val t1 = actor {
      case _ =>
    }
    val l = loggerActor(t1,(x) => msgs += x).start
    val foo : Any = "foo"
    val bar : Any = "bar"
    l ! foo
    l ! bar
    Thread.sleep(1000)
    msgs must ( have size (2) and contain (foo) and contain (bar) )
    t1.stop
    l.stop
  }

  @Test def testSmallestMailboxFirstDispatcher = {
    val t1ProcessedCount = new AtomicInteger(0)
    val t1 = actor {
      case x =>
        Thread.sleep(50) // slow actor
        t1ProcessedCount.incrementAndGet
    }

    val t2ProcessedCount = new AtomicInteger(0)
    val t2 = actor {
      case x => t2ProcessedCount.incrementAndGet
    }
    val d = loadBalancerActor(new SmallestMailboxFirstIterator(t1 :: t2 :: Nil))
    for (i <- 1 to 500) d ! i
    Thread.sleep(6000)
    t1ProcessedCount.get must be < (t2ProcessedCount.get) // because t1 is much slower and thus has a bigger mailbox all the time
    t1.stop
    t2.stop
    d.stop
  }
  
  @Test def testListener = {
    import java.util.concurrent.{ CountDownLatch, TimeUnit }
  
    val latch = new CountDownLatch(2)
    val num = new AtomicInteger(0)
    val i = newActor(() => new Actor with Listeners {
      def receive = listenerManagement orElse {
        case "foo" =>  gossip("bar")
      }
    })
    i.start
      
    def newListener = actor { 
      case "bar" => 
      num.incrementAndGet
      latch.countDown
    }
      
    val a1 = newListener
    val a2 = newListener
    val a3 = newListener
    
    i ! Listen(a1)
    i ! Listen(a2)
    i ! Listen(a3)
    i ! Deafen(a3)

    i ! "foo"

    val done = latch.await(5,TimeUnit.SECONDS)
    done must be (true)
    num.get must be (2)
    i.stop
  }
}