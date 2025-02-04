/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.02 at 10:30:36 上午 CST 
//


package com.hotent.portal.index;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indexColumn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indexColumn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="alias" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="catalog" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="colType" use="required" type="{http://www.jee-soft.cn/index}colType" />
 *       &lt;attribute name="dataMode" use="required" type="{http://www.jee-soft.cn/index}dataMode" />
 *       &lt;attribute name="dataFrom" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dsAlias" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dsName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="colHeight" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="colUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="templateHtml" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isPublic" use="required" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="orgId" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="supportRefesh" type="{http://www.w3.org/2001/XMLSchema}short" default="0" />
 *       &lt;attribute name="refeshTime" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="showEffect" use="required" type="{http://www.jee-soft.cn/index}showEffect" />
 *       &lt;attribute name="memo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indexColumn")
public class IndexColumn {

    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected String alias;
    @XmlAttribute
    protected Long catalog;
    @XmlAttribute(required = true)
    protected short colType;
    @XmlAttribute(required = true)
    protected short dataMode;
    @XmlAttribute
    protected String dataFrom;
    @XmlAttribute
    protected String dsAlias;
    @XmlAttribute
    protected String dsName;
    @XmlAttribute
    protected String colHeight;
    @XmlAttribute
    protected String colUrl;
    @XmlAttribute
    protected String templateHtml;
    @XmlAttribute(required = true)
    protected short isPublic;
    @XmlAttribute
    protected Long orgId;
    @XmlAttribute
    protected Short supportRefesh;
    @XmlAttribute
    protected Integer refeshTime;
    @XmlAttribute(required = true)
    protected short showEffect;
    @XmlAttribute
    protected String memo;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the alias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the value of the alias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlias(String value) {
        this.alias = value;
    }

    /**
     * Gets the value of the catalog property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCatalog() {
        return catalog;
    }

    /**
     * Sets the value of the catalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCatalog(Long value) {
        this.catalog = value;
    }

    /**
     * Gets the value of the colType property.
     * 
     */
    public short getColType() {
        return colType;
    }

    /**
     * Sets the value of the colType property.
     * 
     */
    public void setColType(short value) {
        this.colType = value;
    }

    /**
     * Gets the value of the dataMode property.
     * 
     */
    public short getDataMode() {
        return dataMode;
    }

    /**
     * Sets the value of the dataMode property.
     * 
     */
    public void setDataMode(short value) {
        this.dataMode = value;
    }

    /**
     * Gets the value of the dataFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataFrom() {
        return dataFrom;
    }

    /**
     * Sets the value of the dataFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataFrom(String value) {
        this.dataFrom = value;
    }

    /**
     * Gets the value of the dsAlias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDsAlias() {
        return dsAlias;
    }

    /**
     * Sets the value of the dsAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDsAlias(String value) {
        this.dsAlias = value;
    }

    /**
     * Gets the value of the dsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDsName() {
        return dsName;
    }

    /**
     * Sets the value of the dsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDsName(String value) {
        this.dsName = value;
    }

    /**
     * Gets the value of the colHeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColHeight() {
        return colHeight;
    }

    /**
     * Sets the value of the colHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColHeight(String value) {
        this.colHeight = value;
    }

    /**
     * Gets the value of the colUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColUrl() {
        return colUrl;
    }

    /**
     * Sets the value of the colUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColUrl(String value) {
        this.colUrl = value;
    }

    /**
     * Gets the value of the templateHtml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemplateHtml() {
        return templateHtml;
    }

    /**
     * Sets the value of the templateHtml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemplateHtml(String value) {
        this.templateHtml = value;
    }

    /**
     * Gets the value of the isPublic property.
     * 
     */
    public short getIsPublic() {
        return isPublic;
    }

    /**
     * Sets the value of the isPublic property.
     * 
     */
    public void setIsPublic(short value) {
        this.isPublic = value;
    }

    /**
     * Gets the value of the orgId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrgId() {
        return orgId;
    }

    /**
     * Sets the value of the orgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrgId(Long value) {
        this.orgId = value;
    }

    /**
     * Gets the value of the supportRefesh property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public short getSupportRefesh() {
        if (supportRefesh == null) {
            return ((short) 0);
        } else {
            return supportRefesh;
        }
    }

    /**
     * Sets the value of the supportRefesh property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSupportRefesh(Short value) {
        this.supportRefesh = value;
    }

    /**
     * Gets the value of the refeshTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRefeshTime() {
        return refeshTime;
    }

    /**
     * Sets the value of the refeshTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRefeshTime(Integer value) {
        this.refeshTime = value;
    }

    /**
     * Gets the value of the showEffect property.
     * 
     */
    public short getShowEffect() {
        return showEffect;
    }

    /**
     * Sets the value of the showEffect property.
     * 
     */
    public void setShowEffect(short value) {
        this.showEffect = value;
    }

    /**
     * Gets the value of the memo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemo() {
        return memo;
    }

    /**
     * Sets the value of the memo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemo(String value) {
        this.memo = value;
    }

}
