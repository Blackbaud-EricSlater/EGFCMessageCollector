package com.blackbaud.messagecollector;

import com.blackbaud.messagecollector.job.TwilioScraperJobFactory;
import com.blackbaud.messagecollector.quartz.QuartzManager;
import com.blackbaud.messagecollector.resources.SMSResponseResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCollector extends Service<MessageCollectorConfiguration> {

    final private Logger logger = LoggerFactory.getLogger(MessageCollector.class);

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

        Scheduler scheduler = null;
        try {
             scheduler = StdSchedulerFactory.getDefaultScheduler();
         } catch (SchedulerException ex) {
             logger.info("unable to instantiate quartz scheduler.");
         }

        TwilioScraperJobFactory twilioScraperJobFactory = new TwilioScraperJobFactory();

        environment.addResource(new SMSResponseResource());
        environment.manage(new QuartzManager(scheduler, configuration.getScheduledTaskConfiguration(), twilioScraperJobFactory));
    }

}
