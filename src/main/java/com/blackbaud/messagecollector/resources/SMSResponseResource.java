package com.blackbaud.messagecollector.resources;

import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/sms-response")
public class SMSResponseResource {

    @GET
    @Timed
    @Produces(MediaType.TEXT_PLAIN)
    public String sendSMSResponse() {
        return "Hello";
    }


}
