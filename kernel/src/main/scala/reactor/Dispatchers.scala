/**
 *  Copyright (C) 2009 Scalable Solutions.
 */

package se.scalablesolutions.akka.kernel.reactor

import kernel.actor.Actor

/**
 * Dispatcher factory.
 * <p/>
 * Example usage:
 * <pre/>
 *   val dispatcher = Dispatchers.newEventBasedThreadPoolDispatcher
 *     .withNewThreadPoolWithBoundedBlockingQueue(100)
 *     .setCorePoolSize(16)
 *     .setMaxPoolSize(128)
 *     .setKeepAliveTimeInMillis(60000)
 *     .setRejectionPolicy(new CallerRunsPolicy)
 *     .buildThreadPool
 * </pre>
 * <p/>
 *
 * @author <a href="http://jonasboner.com">Jonas Bon&#233;r</a>
 */
object Dispatchers {
  
  /**
   * Creates an event based dispatcher serving multiple (millions) of actors through a thread pool.
   * Has a fluent builder interface for configuring its semantics.
   */
  def newEventBasedThreadPoolDispatcher = new EventBasedThreadPoolDispatcher

  /**
   * Creates an event based dispatcher serving multiple (millions) of actors through a single thread.
   */
  def newEventBasedSingleThreadDispatcher = new EventBasedSingleThreadDispatcher

  /**
   * Creates an thread based dispatcher serving a single actor through the same single thread.
   * E.g. each actor consumes its own thread.
   */
  def newThreadBasedDispatcher(actor: Actor) = new ThreadBasedDispatcher(actor)
}