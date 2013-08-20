//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.2-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.06.15 at 09:44:57 AM EDT 
//


package edu.harvard.i2b2.crc.datavo.i2b2message;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for response_messageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response_messageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="message_header" type="{http://www.i2b2.org/xsd/hive/msg/1.1/}message_headerType"/>
 *         &lt;element name="request_header" type="{http://www.i2b2.org/xsd/hive/msg/1.1/}request_headerType" minOccurs="0"/>
 *         &lt;element name="response_header" type="{http://www.i2b2.org/xsd/hive/msg/1.1/}response_headerType"/>
 *         &lt;element name="message_body" type="{http://www.i2b2.org/xsd/hive/msg/1.1/}bodyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="response")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response_messageType", propOrder = {
    "messageHeader",
    "requestHeader",
    "responseHeader",
    "messageBody"
})
public class ResponseMessageType {

    @XmlElement(name = "message_header", required = true)
    protected MessageHeaderType messageHeader;
    @XmlElement(name = "request_header")
    protected RequestHeaderType requestHeader;
    @XmlElement(name = "response_header", required = true)
    protected ResponseHeaderType responseHeader;
    @XmlElement(name = "message_body", required = true)
    protected BodyType messageBody;

    /**
     * Gets the value of the messageHeader property.
     * 
     * @return
     *     possible object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.MessageHeaderType }
     *     
     */
    public MessageHeaderType getMessageHeader() {
        return messageHeader;
    }

    /**
     * Sets the value of the messageHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.MessageHeaderType }
     *     
     */
    public void setMessageHeader(MessageHeaderType value) {
        this.messageHeader = value;
    }

    /**
     * Gets the value of the requestHeader property.
     * 
     * @return
     *     possible object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.RequestHeaderType }
     *     
     */
    public RequestHeaderType getRequestHeader() {
        return requestHeader;
    }

    /**
     * Sets the value of the requestHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.RequestHeaderType }
     *     
     */
    public void setRequestHeader(RequestHeaderType value) {
        this.requestHeader = value;
    }

    /**
     * Gets the value of the responseHeader property.
     * 
     * @return
     *     possible object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.ResponseHeaderType }
     *     
     */
    public ResponseHeaderType getResponseHeader() {
        return responseHeader;
    }

    /**
     * Sets the value of the responseHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.ResponseHeaderType }
     *     
     */
    public void setResponseHeader(ResponseHeaderType value) {
        this.responseHeader = value;
    }

    /**
     * Gets the value of the messageBody property.
     * 
     * @return
     *     possible object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.BodyType }
     *     
     */
    public BodyType getMessageBody() {
        return messageBody;
    }

    /**
     * Sets the value of the messageBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link edu.harvard.i2b2.crc.datavo.i2b2message.BodyType }
     *     
     */
    public void setMessageBody(BodyType value) {
        this.messageBody = value;
    }

}