package net.shrine.protocol

import org.scalatest.junit.{ AssertionsForJUnit, ShouldMatchersForJUnit }
import org.junit.Test
import org.junit.Assert.{ assertTrue, assertNotNull }
import xml.Utility
import net.shrine.util.XmlUtil

/**
 * @author Bill Simons
 * @date 4/5/11
 * @link http://cbmi.med.harvard.edu
 * @link http://chip.org
 *       <p/>
 *       NOTICE: This software comes with NO guarantees whatsoever and is
 *       licensed as Lgpl Open Source
 * @link http://www.gnu.org/licenses/lgpl.html
 */
class BroadcastMessageTest extends AssertionsForJUnit with ShouldMatchersForJUnit with XmlSerializableValidator {
  val requestId = 123456
  val masterId = 875875
  val instanceId = 984757
  val resultId1 = 656565
  val resultId2 = 1212121
  val authn = AuthenticationInfo("domain", "username", Credential("cred", false))
  val request = ReadPreviousQueriesRequest("projectId", 10, authn, "username", 20)
  val message = XmlUtil.stripWhitespace(
    <broadcastMessage>
      <requestId>{ requestId }</requestId>
      <request>{ request.toXml }</request>
    </broadcastMessage>)

  @Test
  def testFromXml {
    val actual = BroadcastMessage.fromXml(message)
    
    actual.requestId should equal(requestId)
    actual.request should not be(null)
    actual.request.isInstanceOf[ReadPreviousQueriesRequest] should be(true)
    
    val actualRequest = actual.request.asInstanceOf[ReadPreviousQueriesRequest]
    
    actualRequest.projectId should equal("projectId")
    actualRequest.waitTimeMs should equal(10L)
    actualRequest.authn should equal(authn)
    actualRequest.userId should equal("username")
    actualRequest.fetchSize should equal(20)
  }

  @Test
  def testToXml {
    BroadcastMessage(requestId, request).toXmlString should equal(message.toString)
  }
}