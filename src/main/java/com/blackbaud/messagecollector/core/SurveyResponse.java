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

    @JsonProperty("Short_Name__c")
    private String shortName;

    @JsonProperty("Twilio_Id__c")
    private String twilioId;

    @JsonProperty("Referrer__c")
    private String referrer;

    public SurveyResponse(String phone,
                          Date responseDate,
                          String vote,
                          String shortName,
                          String twilioId,
                          String referrer) {
        this.phone = phone;
        this.responseDate = responseDate;
        this.vote = vote;
        this.shortName = shortName;
        this.twilioId = twilioId;
        this.referrer = referrer;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
}
