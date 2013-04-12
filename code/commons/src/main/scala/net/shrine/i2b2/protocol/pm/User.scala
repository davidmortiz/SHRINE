package net.shrine.i2b2.protocol.pm

import net.shrine.serialization.I2b2Unmarshaller
import xml.NodeSeq
import net.shrine.protocol.Credential
import net.shrine.protocol.AuthenticationInfo

/**
 * @author Bill Simons
 * @date 3/6/12
 * @link http://cbmi.med.harvard.edu
 * @link http://chip.org
 *       <p/>
 *       NOTICE: This software comes with NO guarantees whatsoever and is
 *       licensed as Lgpl Open Source
 * @link http://www.gnu.org/licenses/lgpl.html
 */
final case class User(val fullName: String, val username: String, val domain: String, val credential: Credential, val params: Map[String, String]) {
  def toAuthInfo = AuthenticationInfo(domain, username, credential)
}

object User extends I2b2Unmarshaller[User] {
  override def fromI2b2(nodeSeq: NodeSeq) = {
    
    val userXml = nodeSeq \ "message_body" \ "configure" \ "user"
    
    val params = Map.empty ++ (userXml \ "param").map { param =>
      (param \ "@name").text -> param.text
    }
    
    val fullNameXml = userXml \ "full_name"
    val userNameXml = userXml \ "user_name"
    val domainXml = userXml \ "domain"
    val credentialXml = userXml \ "password"
    
    //NB: Fail loudly
    require(fullNameXml.nonEmpty)
    require(userNameXml.nonEmpty)
    require(domainXml.nonEmpty)
    require(credentialXml.nonEmpty)
    
    User(
      fullNameXml.text,
      userNameXml.text,
      domainXml.text,
      Credential.fromI2b2(credentialXml),
      params)
  }
}