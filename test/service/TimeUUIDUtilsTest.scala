package service

import org.specs2.mutable.Specification


/**
  * Created by quentin on 09/06/17.
  */
class TimeUUIDUtilsTest extends Specification {

  "TimeUUIDUtilsTest" should {
    "be unique and conserve timestamp" in {
      val now = System.currentTimeMillis()
      val uuid = TimeUUIDUtils.fromTimestampMs(now)
      uuid.timestamp() must equalTo(now)
      val uuid2 = TimeUUIDUtils.fromTimestampMs(now)
      uuid2.timestamp() must equalTo(now)
      uuid.toString mustNotEqual uuid2.toString
    }

  }
}
