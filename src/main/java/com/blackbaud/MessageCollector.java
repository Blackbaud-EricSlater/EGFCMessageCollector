package com.blackbaud;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class MessageCollector extends Service<MessageCollectorConfiguration> {
    public static void main(String[] args) throws Exception {
        new MessageCollector().run(args);
    }

    @Override
    public void initialize(Bootstrap<MessageCollectorConfiguration> bootstrap) {
        bootstrap.setName("message-collector");
    }

    @Override
    public void run(MessageCollectorConfiguration configuration,
                    Environment environment) {
        // nothing to do yet
    }

}

