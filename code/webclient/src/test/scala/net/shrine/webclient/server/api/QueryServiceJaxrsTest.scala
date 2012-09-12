package net.shrine.webclient.server.api

import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.matchers.ShouldMatchers
import com.sun.jersey.test.framework.JerseyTest
import net.shrine.webclient.server.MockShrineClient
import org.junit.Test
import net.shrine.webclient.server.QueryService
import net.shrine.webclient.server.OntologyService
import net.shrine.webclient.server.QueryServiceImpl
import net.shrine.protocol.query.And
import net.shrine.protocol.query.Term
import net.shrine.protocol.query.Or
import net.shrine.webclient.shared.domain.SingleInstitutionQueryResult
import net.shrine.webclient.shared.domain.MultiInstitutionQueryResult
import scala.collection.JavaConverters._

/**
 * @author clint
 * @date Aug 7, 2012
 */
final class QueryServiceJaxrsTest extends JerseyTest with ShrineWebclientApiJaxrsTest {
  
  private lazy val toReturn = Map("fooInst" -> new SingleInstitutionQueryResult(123, Map.empty.asJava), "barInst" -> new SingleInstitutionQueryResult(42, Map.empty.asJava))

  private lazy val mockClient = new MockShrineClient(toReturn)
  
  override def queryService: QueryService = new QueryServiceImpl(mockClient)
  
  @Test
  def testSubmit {
    def queryForBreakdown(query: String): Map[String, SingleInstitutionQueryResult] = {
      unmarshal[MultiInstitutionQueryResult](queryResource.path("submit").entity(query).post(classOf[String])).get.asScala.toMap
    }
    
    val queryExpr = And(Term("nuh"), Or(Term("foo"), Term("Bar")))
    
    val queryResult = queryForBreakdown(queryExpr.toXmlString)
    
    mockClient.queryDefinition.expr should equal(queryExpr)
    
    queryResult.size should equal(toReturn.size)
    
    toReturn.map { case (instName, instResult) =>
      queryResult.contains(instName) should be(true)
      queryResult.get(instName).get.count should equal(instResult.count)
      //TODO: BREAKDOWNS OMITTED
    }
  }
}