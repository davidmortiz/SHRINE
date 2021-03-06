package net.shrine.service

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.matchers.ShouldMatchers
import org.spin.tools.RandomTool
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import com.sun.jersey.api.client.UniformInterfaceException
import com.sun.jersey.test.framework.JerseyTest

import javax.ws.rs.Path
import javax.ws.rs.Produces
import net.shrine.client.JerseyShrineClient
import net.shrine.protocol.AggregatedReadInstanceResultsResponse
import net.shrine.protocol.AggregatedReadQueryResultResponse
import net.shrine.protocol.AggregatedRunQueryResponse
import net.shrine.protocol.ApprovedTopic
import net.shrine.protocol.AuthenticationInfo
import net.shrine.protocol.Credential
import net.shrine.protocol.DeleteQueryRequest
import net.shrine.protocol.DeleteQueryResponse
import net.shrine.protocol.EventResponse
import net.shrine.protocol.ObservationResponse
import net.shrine.protocol.ParamResponse
import net.shrine.protocol.PatientResponse
import net.shrine.protocol.QueryResult
import net.shrine.protocol.ReadApprovedQueryTopicsRequest
import net.shrine.protocol.ReadApprovedQueryTopicsResponse
import net.shrine.protocol.ReadInstanceResultsRequest
import net.shrine.protocol.ReadPdoRequest
import net.shrine.protocol.ReadPdoResponse
import net.shrine.protocol.ReadPreviousQueriesRequest
import net.shrine.protocol.ReadPreviousQueriesResponse
import net.shrine.protocol.ReadQueryDefinitionRequest
import net.shrine.protocol.ReadQueryDefinitionResponse
import net.shrine.protocol.ReadQueryInstancesRequest
import net.shrine.protocol.ReadQueryInstancesResponse
import net.shrine.protocol.ReadQueryResultRequest
import net.shrine.protocol.RenameQueryRequest
import net.shrine.protocol.RenameQueryResponse
import net.shrine.protocol.RequestType
import net.shrine.protocol.ResultOutputType
import net.shrine.protocol.RunQueryRequest
import net.shrine.protocol.ShrineRequest
import net.shrine.protocol.ShrineRequestHandler
import net.shrine.protocol.ShrineResponse
import net.shrine.protocol.query.QueryDefinition
import net.shrine.protocol.query.Term
import net.shrine.util.JerseyAppDescriptor
import net.shrine.util.Util

/**
 *
 * @author Clint Gilbert
 * @date Sep 14, 2011
 *
 * @link http://cbmi.med.harvard.edu
 *
 * This software is licensed under the LGPL
 * @link http://www.gnu.org/licenses/lgpl.html
 *
 * Starts a ShrineResource in an embedded HTTP server, sends requests to it, then verifies that the requests don't fail,
 * and that the parameters made it from the client to the ShrineResource successfully.  Uses a mock ShrineRequestHandler, so
 * it doesn't test that correct values are returned by the ShrineResource.
 */
final class ShrineResourceJaxrsTest extends JerseyTest with AssertionsForJUnit with ShouldMatchers {
  private val projectId = "some-project-id"

  private val topicId = "some-topic-id"

  private val userId = "some-user-id"

  private val authenticationInfo = new AuthenticationInfo("some-domain", userId, new Credential("some-val", false))

  private val shrineClient = new JerseyShrineClient(resource.getURI.toString, projectId, authenticationInfo)

  /**
   * We invoked the no-arg superclass constructor, so we must override configure() to provide an AppDescriptor
   * That tells Jersey to instantiate and expose ShrineResource
   */
  override def configure = JerseyAppDescriptor.forResource[ShrineResource].using(MockShrineRequestHandler)
  
  @Test
  def testReadApprovedQueryTopics {
    val response = shrineClient.readApprovedQueryTopics(userId)

    response should not(be(null))

    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readQueryDefinitionParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.readApprovedQueryTopicsParam

    validateCachedParam(param, RequestType.SheriffRequest)

    param.userId should equal(userId)
  }

  @Test
  def testReadPreviousQueries = resetMockThen {
    val fetchSize = 123

    val response = shrineClient.readPreviousQueries(userId, fetchSize)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readQueryDefinitionParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.readPreviousQueriesParam

    validateCachedParam(param, RequestType.UserRequest)

    param.fetchSize should equal(fetchSize)
    param.userId should equal(userId)
  }

  @Test
  def testReadPreviousQueriesUsernameMismatch = resetMockThen {
    intercept[UniformInterfaceException] {
      shrineClient.readPreviousQueries("foo", 123)
    }

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readQueryDefinitionParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
  }

  @Test
  def testRunQuery = resetMockThen {

    val queryDef = QueryDefinition("foo", Term("nuh"))

    def doTestRunQueryResponse(response: AggregatedRunQueryResponse, expectedOutputTypes: Set[ResultOutputType]) {

      response should not(be(null))

      MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
      MockShrineRequestHandler.readPreviousQueriesParam should be(null)
      MockShrineRequestHandler.readQueryInstancesParam should be(null)
      MockShrineRequestHandler.readInstanceResultsParam should be(null)
      MockShrineRequestHandler.readPdoParam should be(null)
      MockShrineRequestHandler.readQueryDefinitionParam should be(null)
      MockShrineRequestHandler.deleteQueryParam should be(null)
      MockShrineRequestHandler.renameQueryParam should be(null)
      MockShrineRequestHandler.readQueryResultParam should be(null)

      val param = MockShrineRequestHandler.runQueryParam

      validateCachedParam(param, RequestType.QueryDefinitionRequest)

      param.outputTypes should equal(expectedOutputTypes)
      param.queryDefinition should equal(queryDef)
      param.topicId should equal(topicId)
    }

    def doTestRunQuery(outputTypes: Set[ResultOutputType]) {
      val responseScalaSet = shrineClient.runQuery(topicId, outputTypes, queryDef)

      doTestRunQueryResponse(responseScalaSet, outputTypes)

      val responseJavaSet = shrineClient.runQuery(topicId, outputTypes, queryDef)

      doTestRunQueryResponse(responseJavaSet, outputTypes)
    }

    Seq(ResultOutputType.values.toSet,
      Set(ResultOutputType.PATIENT_COUNT_XML),
      Set(ResultOutputType.PATIENTSET),
      Set.empty[ResultOutputType]).foreach(doTestRunQuery)
  }

  @Test
  def testReadQueryInstances = resetMockThen {
    val queryId = 123L

    val response = shrineClient.readQueryInstances(queryId)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readQueryDefinitionParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.readQueryInstancesParam

    validateCachedParam(param, RequestType.MasterRequest)

    param.queryId should equal(queryId)
  }

  @Test
  def testReadInstanceResults = resetMockThen {
    val shrineNetworkQueryId = 98765L

    val response = shrineClient.readInstanceResults(shrineNetworkQueryId)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readQueryDefinitionParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.readInstanceResultsParam

    validateCachedParam(param, RequestType.InstanceRequest)

    param.shrineNetworkQueryId should equal(shrineNetworkQueryId)
  }

  @Test
  def testReadPdo = resetMockThen {
    val patientSetId = "patientSetId"
    val optionsXml = <foo><bar/></foo>

    val response = shrineClient.readPdo(patientSetId, optionsXml)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.readQueryDefinitionParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.readPdoParam

    validateCachedParam(param, RequestType.GetPDOFromInputListRequest)

    param.patientSetCollId should equal(patientSetId)
    //Turn NodeSeqs to Strings for reliable comparisons
    param.optionsXml.toString should equal(optionsXml.toString)
  }

  @Test
  def testReadQueryDefinition = resetMockThen {
    val queryId = 3789894L

    val response = shrineClient.readQueryDefinition(queryId)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.readQueryDefinitionParam

    validateCachedParam(param, RequestType.GetRequestXml)

    param.queryId should equal(queryId)
  }

  @Test
  def testDeleteQuery = resetMockThen {
    val queryId = 3789894L

    val response = shrineClient.deleteQuery(queryId)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.deleteQueryParam

    validateCachedParam(param, RequestType.MasterDeleteRequest)

    param.queryId should equal(queryId)
  }

  @Test
  def testRenameQuery = resetMockThen {
    val queryId = 3789894L
    val queryName = "aslkfhkasfh"

    val response = shrineClient.renameQuery(queryId, queryName)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    val param = MockShrineRequestHandler.renameQueryParam

    validateCachedParam(param, RequestType.MasterRenameRequest)

    param.queryId should equal(queryId)
    param.queryName should equal(queryName)
  }

  @Test
  def testReadQueryResult = resetMockThen {
    val queryId = 3789894L

    val response = shrineClient.readQueryResult(queryId)

    response should not(be(null))

    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)

    val param = MockShrineRequestHandler.readQueryResultParam

    validateCachedParam(param, RequestType.GetQueryResult)

    param.queryId should equal(queryId)
  }

  private def validateCachedParam(param: ShrineRequest, expectedRequestType: RequestType) {
    MockShrineRequestHandler.shouldBroadcastParam should be(true)
    param should not(be(null))
    param.projectId should equal(projectId)
    param.authn should equal(authenticationInfo)
    param.requestType should equal(expectedRequestType)
    param.waitTimeMs should equal(ShrineResource.waitTimeMs)
  }

  private def resetMockThen(body: => Any) {
    MockShrineRequestHandler.reset()

    //(Healthy?) paranoia
    MockShrineRequestHandler.readApprovedQueryTopicsParam should be(null)
    MockShrineRequestHandler.readPreviousQueriesParam should be(null)
    MockShrineRequestHandler.runQueryParam should be(null)
    MockShrineRequestHandler.readQueryInstancesParam should be(null)
    MockShrineRequestHandler.readInstanceResultsParam should be(null)
    MockShrineRequestHandler.readPdoParam should be(null)
    MockShrineRequestHandler.readQueryDefinitionParam should be(null)
    MockShrineRequestHandler.deleteQueryParam should be(null)
    MockShrineRequestHandler.renameQueryParam should be(null)
    MockShrineRequestHandler.readQueryResultParam should be(null)

    body
  }

  /**
   * Mock ShrineRequestHandler; stores passed parameters for later inspection.
   * Private, since this is (basically) the enclosing test class's state
   */
  private object MockShrineRequestHandler extends ShrineRequestHandler {
    var shouldBroadcastParam = false

    var readApprovedQueryTopicsParam: ReadApprovedQueryTopicsRequest = _
    var readPreviousQueriesParam: ReadPreviousQueriesRequest = _
    var runQueryParam: RunQueryRequest = _
    var readQueryInstancesParam: ReadQueryInstancesRequest = _
    var readInstanceResultsParam: ReadInstanceResultsRequest = _
    var readPdoParam: ReadPdoRequest = _
    var readQueryDefinitionParam: ReadQueryDefinitionRequest = _
    var deleteQueryParam: DeleteQueryRequest = _
    var renameQueryParam: RenameQueryRequest = _
    var readQueryResultParam: ReadQueryResultRequest = _

    def reset() {
      shouldBroadcastParam = false
      readApprovedQueryTopicsParam = null
      readPreviousQueriesParam = null
      runQueryParam = null
      readQueryInstancesParam = null
      readInstanceResultsParam = null
      readPdoParam = null
      readQueryDefinitionParam = null
      deleteQueryParam = null
      renameQueryParam = null
      readQueryResultParam = null
    }

    import Util.now

    private def setShouldBroadcastAndThen(shouldBroadcast: Boolean)(f: => ShrineResponse): ShrineResponse = {
      try { f } finally {
        shouldBroadcastParam = shouldBroadcast
      }
    }

    override def readApprovedQueryTopics(request: ReadApprovedQueryTopicsRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      readApprovedQueryTopicsParam = request

      ReadApprovedQueryTopicsResponse(Seq(new ApprovedTopic(123L, "some topic")))
    }

    override def readPreviousQueries(request: ReadPreviousQueriesRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      readPreviousQueriesParam = request

      ReadPreviousQueriesResponse(Option("userId"), Option("groupId"), Seq.empty)
    }

    override def readQueryInstances(request: ReadQueryInstancesRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      readQueryInstancesParam = request

      ReadQueryInstancesResponse(999L, "userId", "groupId", Seq.empty)
    }

    override def readInstanceResults(request: ReadInstanceResultsRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      readInstanceResultsParam = request

      AggregatedReadInstanceResultsResponse(1337L, Seq(new QueryResult(123L, 1337L, Some(ResultOutputType.PATIENT_COUNT_XML), 789L, None, None, Some("description"), QueryResult.StatusType.Finished, Some("statusMessage"))))
    }

    override def readPdo(request: ReadPdoRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      readPdoParam = request

      import RandomTool.randomString

      def paramResponse = new ParamResponse(randomString, randomString, randomString)

      ReadPdoResponse(Seq(new EventResponse("event", "patient", None, None, Seq.empty)), Seq(new PatientResponse("patientId", Seq(paramResponse))), Seq(new ObservationResponse(None, "eventId", None, "patientId", None, None, None, "observerCode", "startDate", None, "valueTypeCode", None, None, None, None, None, None, None, Seq(paramResponse))))
    }

    override def readQueryDefinition(request: ReadQueryDefinitionRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      readQueryDefinitionParam = request

      ReadQueryDefinitionResponse(87456L, "name", "userId", now, "<foo/>")
    }

    override def runQuery(request: RunQueryRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      runQueryParam = request

      AggregatedRunQueryResponse(123L, now, "userId", "groupId", request.queryDefinition, 456L, Seq.empty)
    }

    override def deleteQuery(request: DeleteQueryRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      deleteQueryParam = request

      DeleteQueryResponse(56834756L)
    }

    override def renameQuery(request: RenameQueryRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      renameQueryParam = request

      RenameQueryResponse(873468L, "some-name")
    }

    override def readQueryResult(request: ReadQueryResultRequest, shouldBroadcast: Boolean): ShrineResponse = setShouldBroadcastAndThen(shouldBroadcast) {
      readQueryResultParam = request

      AggregatedReadQueryResultResponse(1234567890L, Seq.empty)
    }
  }
}