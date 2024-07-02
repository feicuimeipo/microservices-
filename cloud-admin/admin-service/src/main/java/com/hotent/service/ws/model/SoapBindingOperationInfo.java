package com.hotent.service.ws.model;

import org.nianxi.utils.BeanUtils;

import java.util.ArrayList;
import java.util.List;



public class SoapBindingOperationInfo extends AbstractSoapModel implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private List<SoapParamInfo> inputParams;

	private List<SoapParamInfo> outputParams;

	public List<SoapParamInfo> getInputParams() {
		return inputParams;
	}

	public void setInputParams(List<SoapParamInfo> inputParams) {
		this.inputParams = inputParams;
	}

	public List<SoapParamInfo> getOutputParams() {
		return outputParams;
	}

	public void setOutputParams(List<SoapParamInfo> outputParams) {
		this.outputParams = outputParams;
	}

	public void putInputParam(SoapParamInfo soapParamInfo) {
		if (BeanUtils.isEmpty(inputParams)) {
			this.inputParams = new ArrayList<SoapParamInfo>();
		}
		this.inputParams.add(soapParamInfo);
	}

	public void putOutputParam(SoapParamInfo soapParamInfo) {
		if (BeanUtils.isEmpty(this.outputParams)) {
			this.outputParams = new ArrayList<SoapParamInfo>();
		}
		this.outputParams.add(soapParamInfo);
	}
}