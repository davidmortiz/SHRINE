package net.shrine.adapter.dao.model

import junit.framework.TestCase
import org.scalatest.junit.ShouldMatchersForJUnit
import org.junit.Test
import net.shrine.adapter.dao.scalaquery.rows.QueryResultRow
import net.shrine.protocol.ResultOutputType
import net.shrine.protocol.QueryResult.StatusType
import net.shrine.util.Util
import org.spin.tools.RandomTool
import net.shrine.protocol.QueryResult
import net.shrine.adapter.dao.scalaquery.rows.BreakdownResultRow
import net.shrine.protocol.I2b2ResultEnvelope
import net.shrine.protocol.I2b2ResultEnvelope

/**
 * @author clint
 * @date Nov 1, 2012
 */
final class ShrineQueryResultTest extends TestCase with ShouldMatchersForJUnit {
  import ResultOutputType._

  private val queryId = 999

  private def someId = RandomTool.randomInt

  private def queryResultRow(resultType: ResultOutputType) = QueryResultRow(someId, 
                                                                             queryId, 
                                                                             resultType, 
                                                                             (if (resultType.isError) StatusType.Error else StatusType.Finished), 
                                                                             (if (resultType.isError) None else Some(100)), 
                                                                             Util.now)

  private val countQueryResultRow = queryResultRow(PATIENT_COUNT_XML)

  private val breakdownQueryResultRow1 = queryResultRow(PATIENT_AGE_COUNT_XML)

  private val breakdownQueryResultRow2 = queryResultRow(PATIENT_GENDER_COUNT_XML)

  private val errorQueryResultRow1 = queryResultRow(ERROR)

  private val errorQueryResultRow2 = queryResultRow(ERROR)

  private val resultRows = Seq(countQueryResultRow, breakdownQueryResultRow1, breakdownQueryResultRow2, errorQueryResultRow1, errorQueryResultRow2)

  private val countRows = Seq(Count(someId, countQueryResultRow.id, 99, 100, Util.now), Count(someId, countQueryResultRow.id, 123, 124, Util.now))

  private val breakdownRows = Map(PATIENT_AGE_COUNT_XML -> Seq(BreakdownResultRow(someId, breakdownQueryResultRow1.id, "x", 1, 2), BreakdownResultRow(someId, breakdownQueryResultRow1.id, "y", 2, 3)),
                                   PATIENT_GENDER_COUNT_XML -> Seq(BreakdownResultRow(someId, breakdownQueryResultRow2.id, "a", 9, 10), BreakdownResultRow(someId, breakdownQueryResultRow2.id, "b", 10, 11)))

  private val errorRows = Seq(ShrineError(someId, errorQueryResultRow1.id, "foo"), ShrineError(someId, errorQueryResultRow2.id, "bar"))

  @Test
  def testToQueryResults {
    val Some(shrineQueryResult) = ShrineQueryResult.fromRows(resultRows, countRows, breakdownRows, errorRows)

    shrineQueryResult.copy(count = None).toQueryResults should equal(Option(errorRows.head.toQueryResult))
    shrineQueryResult.copy(count = None, errors = Nil).toQueryResults should equal(None)
    
    val expected = Option(countRows.head.toQueryResult.withBreakdowns(Map(PATIENT_AGE_COUNT_XML -> I2b2ResultEnvelope(PATIENT_AGE_COUNT_XML, Map("x" -> 2, "y" -> 3)), 
                                                                          PATIENT_GENDER_COUNT_XML -> I2b2ResultEnvelope(PATIENT_GENDER_COUNT_XML, Map("a" -> 10, "b" -> 11)))))
    
    shrineQueryResult.toQueryResults should equal(expected)
  }

  @Test
  def testFromRows {
    ShrineQueryResult.fromRows(Nil, Nil, Map.empty, Nil) should be(None)
    ShrineQueryResult.fromRows(Nil, countRows, breakdownRows, errorRows) should be(None)

    val Some(shrineQueryResult) = ShrineQueryResult.fromRows(resultRows, countRows, breakdownRows, errorRows)

    shrineQueryResult.count should equal(Some(countRows.head))
    shrineQueryResult.errors should equal(errorRows)
    shrineQueryResult.breakdowns.toSet should equal(Set(Breakdown(breakdownQueryResultRow1.id, PATIENT_AGE_COUNT_XML, Map("x" -> ObfuscatedPair(1, 2), "y" -> ObfuscatedPair(2, 3))),
                                                        Breakdown(breakdownQueryResultRow2.id, PATIENT_GENDER_COUNT_XML, Map("a" -> ObfuscatedPair(9, 10), "b" -> ObfuscatedPair(10, 11)))))
  }
}