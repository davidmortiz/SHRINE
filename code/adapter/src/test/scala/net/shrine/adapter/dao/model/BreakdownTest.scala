package net.shrine.adapter.dao.model

import junit.framework.TestCase
import org.scalatest.junit.ShouldMatchersForJUnit
import org.junit.Test
import net.shrine.protocol.ResultOutputType
import net.shrine.adapter.dao.scalaquery.rows.BreakdownResultRow

/**
 * @author clint
 * @date Nov 1, 2012
 */
final class BreakdownTest extends TestCase with ShouldMatchersForJUnit {
  import ResultOutputType._
  
  @Test
  def testFromRows {
    ResultOutputType.breakdownTypes.foreach { resultType =>
      Breakdown.fromRows(resultType, Seq.empty) should be(None)
    }
    
    ResultOutputType.nonBreakdownTypes.foreach { resultType =>
      intercept[Exception] {
        Breakdown.fromRows(resultType, Seq.empty)
      }
    }

    //TODO: test handling rows with different resultIds
    
    val rows = Seq(BreakdownResultRow(1, 123, "foo", 42L, 43L), BreakdownResultRow(2, 123, "bar", 99L, 100L))
    
    ResultOutputType.breakdownTypes.foreach { resultType =>
      val Some(breakdown) = Breakdown.fromRows(resultType, rows)
      
      breakdown.resultId should equal(123)
      breakdown.resultType should equal(resultType)
      breakdown.data should equal(Map("foo" -> ObfuscatedPair(42, 43), "bar" -> ObfuscatedPair(99, 100)))
    }
  }
}