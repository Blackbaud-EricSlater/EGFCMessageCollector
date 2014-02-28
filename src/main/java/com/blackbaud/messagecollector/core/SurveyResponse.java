package com.blackbaud.messagecollector.core;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class SurveyResponse {

    @JsonProperty("Phone__c")
    private String phone;

    @JsonProperty("Response_Date__c")
    private Date responseDate;

    @JsonProperty("Vote__c")
    private String vote;

    @JsonProperty("Twilio_Id__c")
    private String twilioId;

    public SurveyResponse(String phone,
                          Date responseDate,
                          String vote,
                          String twilioId) {
        this.phone = phone;
        this.responseDate = responseDate;
        this.vote = vote;
        this.twilioId = twilioId;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
    public String getTwilioId() {
        return twilioId;
    }

    public void setTwilioId(String twilioId) {
        this.twilioId = twilioId;
    }
}
