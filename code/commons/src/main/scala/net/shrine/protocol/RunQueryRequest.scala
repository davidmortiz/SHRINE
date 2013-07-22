package net.shrine.protocol

import net.shrine.serializers.crc.CRCRequestType.QueryDefinitionRequestType
import xml._
import net.shrine.util.XmlUtil

/**
 * @author Bill Simons
 * @date 3/9/11
 * @link http://cbmi.med.harvard.edu
 * @link http://chip.org
 *       <p/>
 *       NOTICE: This software comes with NO guarantees whatsoever and is
 *       licensed as Lgpl Open Source
 * @link http://www.gnu.org/licenses/lgpl.html
 * 
 * NB: this is a case class to get a structural equality contract in hashCode and equals, mostly for testing
 */
final case class RunQueryRequest(
    override val projectId: String,
    override val waitTimeMs: Long,
    override val authn: AuthenticationInfo,
    val topicId: String,
    val outputTypes: Set[ResultOutputType],
    val queryDefinitionXml: String) extends ShrineRequest(projectId, waitTimeMs, authn) with TranslatableRequest[RunQueryRequest] {

  val requestType = QueryDefinitionRequestType

  def toXml = XmlUtil.stripWhitespace(
    <runQuery>
      {headerFragment}
      <topicId>{topicId}</topicId>
      <outputTypes>
      {
        outputTypes map {x =>
          <outputType>{new Text(x.toString)}</outputType>
        }
      }
      </outputTypes>
      <queryDefinition>{queryDefinitionXml}</queryDefinition>
    </runQuery>)

  def handle(handler: ShrineRequestHandler) = {
    handler.runQuery(this)
  }

  protected def i2b2MessageBody = XmlUtil.stripWhitespace(
    <message_body>
      <ns4:psmheader>
        <user group={authn.domain} login={authn.username}>{authn.username}</user>
        <patient_set_limit>0</patient_set_limit>
        <estimated_time>0</estimated_time>
        <request_type>CRC_QRY_runQueryInstance_fromQueryDefinition</request_type>
      </ns4:psmheader>
      <ns4:request xsi:type="ns4:query_definition_requestType" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        {XML.loadString(queryDefinitionXml)}
        <result_output_list>
        {
          for {
            i <- 1 to outputTypes.size
          } yield {
            <result_output priority_index={i.toString} name={outputTypes.toList(i - 1).toString.toLowerCase}/>
          }
        }
        </result_output_list>
      </ns4:request>
      <shrine><queryTopicID>{topicId}</queryTopicID></shrine>
    </message_body>)

  def withProject(proj: String) = this.copy(projectId = proj)

  def withAuthn(ai: AuthenticationInfo) = this.copy(authn = ai)

  def withQueryDefinition(qDef: String) = this.copy(queryDefinitionXml = qDef)
}

object RunQueryRequest extends I2b2Umarshaller[RunQueryRequest] with ShrineRequestUnmarshaller[RunQueryRequest] {

  val neededI2b2Namespace = "http://www.i2b2.org/xsd/cell/crc/psm/1.1/"
  
  def fromI2b2(nodeSeq: NodeSeq): RunQueryRequest = {
    val queryDefNode = nodeSeq \ "message_body" \ "request" \ "query_definition"

    val queryDefString = queryDefNode.head match {
      //NB: elem.scope.getPrefix(neededI2b2Namespace) will return null if elem isn't part of a larger XML chunk that has
      //the http://www.i2b2.org/xsd/cell/crc/psm/1.1/ declared
      case elem: Elem => elem.copy(elem.scope.getPrefix(neededI2b2Namespace)).toString
      case _ => throw new Exception("When unmarshalling a RunQueryRequest, encountered unexpected XML: '" + queryDefNode + "', <query_definition> might be missing.")
    }

    new RunQueryRequest(
      i2b2ProjectId(nodeSeq),
      i2b2WaitTimeMs(nodeSeq),
      i2b2AuthenticationInfo(nodeSeq),
      (nodeSeq \ "message_body" \ "shrine" \ "queryTopicID").text,
      determineI2b2OutputTypes(nodeSeq \ "message_body" \ "request" \ "result_output_list"),
      queryDefString)
  }

  private def determineI2b2OutputTypes(nodeSeq: NodeSeq): Set[ResultOutputType] = {
    val sequence = (nodeSeq \ "result_output") collect {
      case x if (x \ "@name").toString.equalsIgnoreCase("patientset") => ResultOutputType.PATIENTSET
      case x if (x \ "@name").toString.equalsIgnoreCase("patient_count_xml") => ResultOutputType.PATIENT_COUNT_XML
    }

    sequence.toSet
  }

  private def determineShrineOutputTypes(nodeSeq: NodeSeq): Set[ResultOutputType] =
    (nodeSeq \ "outputType").map {x =>
      ResultOutputType.valueOf(x.text)
    }.toSet

  def fromXml(nodeSeq: NodeSeq) = {
    new RunQueryRequest(
      shrineProjectId(nodeSeq),
      shrineWaitTimeMs(nodeSeq),
      shrineAuthenticationInfo(nodeSeq),
      (nodeSeq \ "topicId").text,
      determineShrineOutputTypes(nodeSeq \ "outputTypes"),
      (nodeSeq \ "queryDefinition").text)
  }
}