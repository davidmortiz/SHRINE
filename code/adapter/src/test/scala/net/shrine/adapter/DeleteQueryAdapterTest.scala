package net.shrine.adapter

import org.springframework.test.AbstractDependencyInjectionSpringContextTests
import org.scalatest.junit.ShouldMatchersForJUnit
import org.junit.Test
import org.spin.tools.crypto.signature.Identity
import net.shrine.protocol.BroadcastMessage
import net.shrine.protocol.DeleteQueryRequest
import net.shrine.protocol.AuthenticationInfo
import net.shrine.protocol.Credential
import net.shrine.protocol.ErrorResponse
import net.shrine.protocol.DeleteQueryResponse
import net.shrine.protocol.query.Term

/**
 * @author clint
 * @date Nov 27, 2012
 */
final class DeleteQueryAdapterTest extends AbstractDependencyInjectionSpringContextTests with AdapterDbTest with ShouldMatchersForJUnit {
  private val id = new Identity("some-domain", "some-user")
  private val authn = AuthenticationInfo("Some-domain", "some-user", Credential("aslkdjkaljsd", false))

  @Test
  def testProcessRequest = afterCreatingTables {
    val adapter = new DeleteQueryAdapter(dao)

    val queryId = 123

    {
      val DeleteQueryResponse(returnedId) = adapter.processRequest(id, new BroadcastMessage(123L, new DeleteQueryRequest("proj", 1000L, authn, queryId)))

      returnedId should equal(queryId)
    }

    dao.insertQuery(queryId, "some-query", authn, Term("foo"))

    {
      val Some(query) = dao.findQueryByNetworkId(queryId)

      query.networkId should equal(queryId)
    }

    {
      //try to delete a bogus query
      val DeleteQueryResponse(returnedId) = adapter.processRequest(id, new BroadcastMessage(123L, new DeleteQueryRequest("proj", 1000L, authn, 999)))

      returnedId should equal(999)

      //Query in the DB should be unchanged
      val Some(query) = dao.findQueryByNetworkId(queryId)

      query.networkId should equal(queryId)
    }
    
    {
      //try to delete a real query
      val DeleteQueryResponse(returnedId) = adapter.processRequest(id, new BroadcastMessage(123L, new DeleteQueryRequest("proj", 1000L, authn, queryId)))

      returnedId should equal(queryId)

      //Query in the DB should be gone
      dao.findQueryByNetworkId(queryId) should be(None)
    }
  }
}