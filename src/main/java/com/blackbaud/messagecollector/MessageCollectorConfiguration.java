package com.blackbaud.messagecollector;

import com.blackbaud.messagecollector.configuration.ScheduledTaskConfiguration;
import com.yammer.dropwizard.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MessageCollectorConfiguration extends Configuration {

    @Valid
    @JsonProperty
    @NotNull
    private ScheduledTaskConfiguration scheduledTask = new ScheduledTaskConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    public ScheduledTaskConfiguration getScheduledTaskConfiguration() {
        return scheduledTask;
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }

}
