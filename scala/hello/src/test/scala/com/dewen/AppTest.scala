package com.dewen

import org.junit.Assert._
import org.junit._

@Test
class AppTest {

  @Test
  def testOK() = assertTrue(true)

  //    @Test
  //    def testKO() = assertTrue(false)
  @Test
  def sayHello(): Unit = {
    println("helloworld")
  }

}


