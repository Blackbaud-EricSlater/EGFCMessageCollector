package com.blackbaud.messagecollector.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScheduledTaskConfiguration {

    @JsonProperty
    private String twilioScraperJobFrequency;

    public String getTwilioScraperJobFrequency() {
        return twilioScraperJobFrequency;
    }

}
