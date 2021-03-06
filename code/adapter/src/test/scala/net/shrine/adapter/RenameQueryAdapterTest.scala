package net.shrine.adapter

import org.scalatest.junit.ShouldMatchersForJUnit
import org.junit.Test
import org.spin.tools.crypto.signature.Identity
import net.shrine.protocol.AuthenticationInfo
import net.shrine.protocol.Credential
import net.shrine.protocol.BroadcastMessage
import net.shrine.protocol.DeleteQueryRequest
import net.shrine.protocol.RenameQueryRequest
import net.shrine.protocol.RenameQueryResponse
import net.shrine.protocol.query.Term
import net.shrine.adapter.spring.AbstractShrineJUnitSpringTest
import net.shrine.adapter.dao.squeryl.AbstractSquerylAdapterTest

/**
 * @author clint
 * @date Nov 27, 2012
 */
final class RenameQueryAdapterTest extends AbstractShrineJUnitSpringTest with AbstractSquerylAdapterTest with AdapterTestHelpers with ShouldMatchersForJUnit {
  @Test
  def testProcessRequest = afterCreatingTables {
    val name = "blarg"
    
    val newName = "nuh"
    
    val adapter = new RenameQueryAdapter(dao)
    
    //No queries in the DB, should still "work"
    {
      val RenameQueryResponse(returnedId, returnedName) = adapter.processRequest(id, new BroadcastMessage(123L, new RenameQueryRequest("proj", 1000L, authn, queryId, name)))
      
      returnedId should equal(queryId)
      returnedName should equal(name)
    }
    
    //add a query to the db
    dao.insertQuery(localMasterId, queryId, name, authn, Term("foo"))

    //sanity check that it's there
    {
      val Some(query) = dao.findQueryByNetworkId(queryId)

      query.networkId should equal(queryId)
    }
    
    {
      //try to rename a bogus query
      val RenameQueryResponse(returnedId, returnedName) = adapter.processRequest(id, new BroadcastMessage(123L, new RenameQueryRequest("proj", 1000L, authn, bogusQueryId, newName)))

      returnedId should equal(bogusQueryId)
      returnedName should equal(newName)

      //Query in the DB should be unchanged
      val Some(query) = dao.findQueryByNetworkId(queryId)

      query.name should equal(name)
    }
    
    {
      //try to rename a real query
      val RenameQueryResponse(returnedId, returnedName) = adapter.processRequest(id, new BroadcastMessage(123L, new RenameQueryRequest("proj", 1000L, authn, queryId, newName)))

      returnedId should equal(queryId)
      returnedName should equal(newName)

      //Query in the DB should be renamed
      val Some(query) = dao.findQueryByNetworkId(queryId)
      
      query.name should equal(newName)
    }
  }
  
  @Test
  def testProcessRequestBadRequest {
    val adapter = new RenameQueryAdapter(dao)

    intercept[Exception] {
      adapter.processRequest(id, new BroadcastMessage(123L, new DeleteQueryRequest("proj", 1000L, authn, queryId)))
    }
  }
}