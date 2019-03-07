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


    //    val version = Futures.transform(result, new com.google.common.base.Function[ResultSet, String] {
    //      def apply(rs: ResultSet): String = rs.one.getString("release_version")
    //    })

    Futures.addCallback(result, new FutureCallback[Result[Subscriber]]() {

      override def onSuccess(result: Result[Subscriber]) = {

        val subscriber = Option(result.one)

        if (subscriber.nonEmpty) {
          // log.info("[{}] OK, get MO successfully, duration: {} ms", cdrId, getDuration(timer))
          promise.success(subscriber)
        } else {
          //          log.info("[{}] WARN, MO not found in DB, duration: {} ms", cdrId, getDuration(timer))
          promise.success(subscriber)
        }
      }

      override def onFailure(t: Throwable) = {
        //        log.error("[{}] FAIL, get MO unsuccessfully, duration: {} ms, ex: {}", cdrId, getDuration(timer), mkString(t))

        promise.failure(t)
      }
    }, ForkJoinPool.commonPool)

    promise.future
  }

  def saveSubscriber(msisdn: Long, dto: SubscriberDto): Future[Void] = {

    val subscriber = Subscriber(msisdn, dto.subscriberType, dto.segmentType, dto.languageType, dto.hlrType, dto.operatorType, new Date)
    val promise = Promise[Void]()

    Futures.addCallback(subscriberMapper.saveAsync(subscriber), new FutureCallback[Void]() {

      override def onSuccess(result: Void) = {

          // log.info("[{}] OK, get MO successfully, duration: {} ms", cdrId, getDuration(timer))
        promise.success(null)
      }

      override def onFailure(t: Throwable) = {
        //        log.error("[{}] FAIL, get MO unsuccessfully, duration: {} ms, ex: {}", cdrId, getDuration(timer), mkString(t))

        promise.failure(t)
      }
    }, ForkJoinPool.commonPool)

    promise.future
  }
}