package net.shrine.protocol

import xml.{Utility, NodeBuffer, NodeSeq}
import net.shrine.serializers.crc.CRCRequestType
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
 */
abstract class ShrineRequest(val projectId: String, val waitTimeMs: Long, val authn: AuthenticationInfo) extends XmlMarshaller with I2b2Marshaller {
  protected def headerFragment: NodeBuffer = <projectId>{projectId}</projectId><waitTimeMs>{waitTimeMs}</waitTimeMs> &+ authn.toXml

  protected def i2b2MessageBody: NodeSeq

  def handle(handler: ShrineRequestHandler): ShrineResponse

  val requestType: CRCRequestType

  def toI2b2 = XmlUtil.stripWhitespace(
    <ns6:request xmlns:ns4="http://www.i2b2.org/xsd/cell/crc/psm/1.1/" xmlns:ns8="http://sheriff.shrine.net/" xmlns:ns7="http://www.i2b2.org/xsd/cell/crc/psm/querydefinition/1.1/" xmlns:ns3="http://www.i2b2.org/xsd/cell/crc/pdo/1.1/" xmlns:ns5="http://www.i2b2.org/xsd/hive/plugin/" xmlns:ns2="http://www.i2b2.org/xsd/hive/pdo/1.1/" xmlns:ns6="http://www.i2b2.org/xsd/hive/msg/1.1/">
      <message_header>
        <proxy>
          <redirect_url>https://localhost/shrine/QueryToolService/request</redirect_url>
        </proxy>
        <sending_application>
          <application_name>i2b2_QueryTool</application_name>
          <application_version>0.2</application_version>
        </sending_application>
        <sending_facility>
          <facility_name>SHRINE</facility_name>
        </sending_facility>
        <receiving_application>
          <application_name>i2b2_DataRepositoryCell</application_name>
          <application_version>0.2</application_version>
        </receiving_application>
        <receiving_facility>
          <facility_name>SHRINE</facility_name>
        </receiving_facility>
        {authn.toI2b2}
        <message_type>
          <message_code>Q04</message_code>
          <event_type>EQQ</event_type>
        </message_type>
        <message_control_id>
          <message_num>EQ7Szep1Md11K4E7zEc99</message_num>
          <instance_num>0</instance_num>
        </message_control_id>
        <processing_id>
          <processing_id>P</processing_id>
          <processing_mode>I</processing_mode>
        </processing_id>
        <accept_acknowledgement_type>AL</accept_acknowledgement_type>
        <project_id>{projectId}</project_id>
        <country_code>US</country_code>
      </message_header>
      <request_header>
        <result_waittime_ms>{waitTimeMs}</result_waittime_ms>
      </request_header>
      {i2b2MessageBody}
    </ns6:request>)
}

object ShrineRequest extends I2b2Umarshaller[ShrineRequest] with XmlUnmarshaller[ShrineRequest]{
  def fromI2b2(i2b2Request: NodeSeq): ShrineRequest = {
    i2b2Request match {
      case x if isPsmRequest(x) => parsePsmRequest(x)
      case x if isPdoRequest(x) => parsePdoRequest(x)
      case x if isSheriffRequest(x) => parseSheriffRequest(x)
      case _ => null
    }
  }

  private def isPsmRequest(requestXml: NodeSeq): Boolean = {
    (requestXml \ "message_body" \ "psmheader").nonEmpty
  }

  private def isPdoRequest(requestXml: NodeSeq): Boolean = {
    (requestXml \ "message_body" \ "pdoheader").nonEmpty
  }

  private def isSheriffRequest(requestXml: NodeSeq): Boolean = {
    (requestXml \ "message_body" \ "sheriff_header").nonEmpty
  }

  private def parsePsmRequest(requestXml: NodeSeq): ShrineRequest = {
    (requestXml \ "message_body" \ "psmheader" \ "request_type").text match {
      case "CRC_QRY_getQueryResultInstanceList_fromQueryInstanceId" => ReadInstanceResultsRequest.fromI2b2(requestXml)
      case "CRC_QRY_getQueryMasterList_fromUserId" => ReadPreviousQueriesRequest.fromI2b2(requestXml)
      case "CRC_QRY_getRequestXml_fromQueryMasterId" => ReadQueryDefinitionRequest.fromI2b2(requestXml)
      case "CRC_QRY_getQueryInstanceList_fromQueryMasterId" => ReadQueryInstancesRequest.fromI2b2(requestXml)
      case "CRC_QRY_runQueryInstance_fromQueryDefinition" => RunQueryRequest.fromI2b2(requestXml)
      case "CRC_QRY_renameQueryMaster" => RenameQueryRequest.fromI2b2(requestXml)
      case "CRC_QRY_deleteQueryMaster" => DeleteQueryRequest.fromI2b2(requestXml)
      case _ => null
    }
  }

  private def parsePdoRequest(requestXml: NodeSeq): ReadPdoRequest = {
    (requestXml \ "message_body" \ "pdoheader" \ "request_type").text match {
      case "getPDO_fromInputList" =>
        ReadPdoRequest.fromI2b2(requestXml)
      case _ => null
    }
  }

  private def parseSheriffRequest(requestXml: NodeSeq): ReadApprovedQueryTopicsRequest = {
    ReadApprovedQueryTopicsRequest.fromI2b2(requestXml)
  }

  def fromXml(nodeSeq: NodeSeq) = {
    nodeSeq match {
      case x if (x.head.label == "deleteQuery") => DeleteQueryRequest.fromXml(nodeSeq)
      case x if (x.head.label == "readApprovedQueryTopics") => ReadApprovedQueryTopicsRequest.fromXml(nodeSeq)
      case x if (x.head.label == "readInstanceResults") => ReadInstanceResultsRequest.fromXml(nodeSeq)
      case x if (x.head.label == "readPdo") => ReadPdoRequest.fromXml(nodeSeq)
      case x if (x.head.label == "readPreviousQueries") => ReadPreviousQueriesRequest.fromXml(nodeSeq)
      case x if (x.head.label == "readQueryDefinition") => ReadQueryDefinitionRequest.fromXml(nodeSeq)
      case x if (x.head.label == "readQueryInstances") => ReadQueryInstancesRequest.fromXml(nodeSeq)
      case x if (x.head.label == "renameQuery") => RenameQueryRequest.fromXml(nodeSeq)
      case x if (x.head.label == "runQuery") => RunQueryRequest.fromXml(nodeSeq)
      case _ => null
    }
  }

}