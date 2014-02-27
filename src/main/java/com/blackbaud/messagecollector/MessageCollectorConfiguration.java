package com.blackbaud.messagecollector;

import com.blackbaud.messagecollector.configuration.ScheduledTaskConfiguration;
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MessageCollectorConfiguration extends Configuration {

    @Valid
    @JsonProperty
    @NotNull
    private ScheduledTaskConfiguration scheduledTask = new ScheduledTaskConfiguration();

    public ScheduledTaskConfiguration getScheduledTaskConfiguration() {
        return scheduledTask;
    }

}
