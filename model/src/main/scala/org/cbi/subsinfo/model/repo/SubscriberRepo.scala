package org.cbi.subsinfo.model.repo

import java.util.Date
import java.util.concurrent.ForkJoinPool

import com.datastax.driver.mapping.Result
import com.google.common.util.concurrent.{FutureCallback, Futures}
import org.cbi.subsinfo.model.dao.Subscriber
import org.cbi.subsinfo.model.dto.SubscriberDto

import scala.concurrent.{Future, Promise}

trait SubscriberRepo extends DB {

  def getSubscriber(msisdn: Long): Future[Option[Subscriber]] = {

    val rsf = session.executeAsync(subscriberAccessor.getSt(msisdn))
    val result = subscriberMapper.mapAsync(rsf)

    val promise = Promise[Option[Subscriber]]()

    Futures.addCallback(result, new FutureCallback[Result[Subscriber]]() {

      override def onSuccess(result: Result[Subscriber]) = {

        val subscriber = Option(result.one)

        if (subscriber.nonEmpty)
          promise.success(subscriber)
          else
          promise.success(subscriber)
      }

      override def onFailure(t: Throwable) =
        promise.failure(t)
    }, ForkJoinPool.commonPool)

    promise.future
  }

  def saveSubscriber(msisdn: Long, dto: SubscriberDto): Future[Void] = {

    val subscriber = Subscriber(msisdn, dto.subscriberType, dto.segmentType, dto.languageType, dto.hlrType, dto.operatorType, new Date)
    val promise = Promise[Void]()

    Futures.addCallback(subscriberMapper.saveAsync(subscriber), new FutureCallback[Void]() {

      override def onSuccess(result: Void) =
        promise.success(null)

      override def onFailure(t: Throwable) =
        promise.failure(t)
    }, ForkJoinPool.commonPool)

    promise.future
  }
}