package net.shrine.service

import scala.collection.JavaConverters._
import org.easymock.EasyMock.{ expect => invoke }
import org.easymock.EasyMock.isA
import org.easymock.EasyMock.same
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.junit.ShouldMatchersForJUnit
import org.scalatest.mock.EasyMockSugar
import org.spin.client.AgentException
import org.spin.tools.crypto.signature.CertID
import org.spin.tools.crypto.signature.Identity
import org.spin.tools.crypto.Envelope
import org.spin.tools.Interval
import net.shrine.aggregation.Aggregator
import net.shrine.aggregation.SpinResultEntry
import net.shrine.protocol.AuthenticationInfo
import net.shrine.protocol.BroadcastMessage
import net.shrine.protocol.Credential
import net.shrine.protocol.DeleteQueryRequest
import net.shrine.protocol.ErrorResponse
import net.shrine.protocol.ShrineResponse
import org.spin.message.QueryInfo
import org.spin.message.QueryInput
import org.spin.message.AckNack
import org.spin.message.serializer.BasicSerializer
import org.spin.message.ResultSet
import org.spin.message.Failure
import org.spin.message.Result
import org.spin.message.StatusCode
import net.shrine.protocol.ReadQueryInstancesRequest
import net.shrine.protocol.ReadQueryInstancesResponse
import org.spin.client.SpinClient
import scala.concurrent.Future
import org.spin.message.serializer.Stringable
import org.spin.client.Credentials
import org.spin.tools.config.DefaultPeerGroups
import net.shrine.protocol.QueryInstance

/**
 * @author Bill Simons
 * @date 3/30/11
 * @link http://cbmi.med.harvard.edu
 * @link http://chip.org
 *       <p/>
 *       NOTICE: This software comes with NO guarantees whatsoever and is
 *       licensed as Lgpl Open Source
 * @link http://www.gnu.org/licenses/lgpl.html
 */
class ShrineServiceTest extends AssertionsForJUnit with ShouldMatchersForJUnit with EasyMockSugar {

  @Test
  def testReadQueryInstances {
    val projectId = "foo"
    val queryId = 123L
    val authn = AuthenticationInfo("some-domain", "some-username", Credential("blarg", false))
    val req = ReadQueryInstancesRequest(projectId, 1L, authn, queryId)

    val service = new ShrineService(null, null, true, null, null)

    val response = service.readQueryInstances(req).asInstanceOf[ReadQueryInstancesResponse]

    response should not be (null)
    response.groupId should equal(projectId)
    response.queryMasterId should equal(queryId)
    response.userId should equal(authn.username)

    val Seq(instance) = response.queryInstances

    instance.startDate should not be (null)
    instance.endDate should not be (null)
    instance.startDate should equal(instance.endDate)
    instance.groupId should equal(projectId)
    instance.queryInstanceId should equal(queryId.toString)
    instance.queryMasterId should equal(queryId.toString)
    instance.userId should equal(authn.username)
  }
}