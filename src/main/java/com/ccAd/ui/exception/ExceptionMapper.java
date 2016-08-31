package com.ccAd.ui.exception;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.ccAd.common.exception.NoActiveCampaignException;

/**
 * This class maps an application exception into a Jersey error response.
 * 
 * @author Juliano DeCarvalho
 */
@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Throwable> {

	@Context
	private HttpHeaders headers;

	@Override
	public Response toResponse(Throwable t) {
		Status status = Status.BAD_REQUEST;
		if (t instanceof NoActiveCampaignException) {
			status = Status.NO_CONTENT;
		}

		ExceptionWrapper wrapper = new ExceptionWrapper(status, t.getMessage());
		return Response.status(Status.BAD_REQUEST).entity(wrapper).type(headers.getMediaType()).build();
	}

}
