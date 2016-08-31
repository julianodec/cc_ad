package com.ccAd.ui.exception;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is a wrapper around application exceptions that need to be
 * propagated to the UI as a Jersey resource.
 * 
 * @author Juliano DeCarvalho
 */
@XmlRootElement
public class ExceptionWrapper {

	private int status;
	private String msg;

	public ExceptionWrapper() {
	}

	public ExceptionWrapper(Status status, String msg) {
		this.status = status.getStatusCode();
		this.msg = msg;
	}

	@XmlElement
	public boolean isError() {
		return true;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
