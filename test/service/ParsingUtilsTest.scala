package service

import org.scalatest.FunSuite

/**
  * Created by quentin on 09/11/17.
  */
class ParsingUtilsTest extends FunSuite {

  test("testToJavaInteger") {
    assert(ParsingUtils.toJavaInteger("Nan") === null)
    assert(ParsingUtils.toJavaInteger(" 12  ") === 12)
    assert(ParsingUtils.toJavaInteger("99995") === 99995)
  }

  test("testToJavaLong") {
    assert(ParsingUtils.toJavaLong("Nan") === null)
    assert(ParsingUtils.toJavaLong(" 12  ") === 12)
    assert(ParsingUtils.toJavaLong("99995") === 99995)
  }

}
