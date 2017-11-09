package service

import com.google.common.util.concurrent.ListenableFuture

import scala.collection.mutable

class FutureLimiter[A] (val count:Int = 100){
  val futures = mutable.Queue[ListenableFuture[A]]()
  def add(future: ListenableFuture[A]) = {
    futures += future
    if(futures.size >= count){
      futures(0).get()
      futures.drop(0)
    }
  }
  def waitAll(): Unit ={
    futures.foreach(_.get())
  }
}
