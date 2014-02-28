package com.blackbaud.messagecollector;

import com.blackbaud.messagecollector.dao.MessageDAO;
import com.blackbaud.messagecollector.job.DataConverterJobFactory;
import com.blackbaud.messagecollector.quartz.QuartzManager;
import com.blackbaud.messagecollector.resources.SMSResponseResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.migrations.MigrationsBundle;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCollector extends Service<MessageCollectorConfiguration> {

    final private Logger logger = LoggerFactory.getLogger(MessageCollector.class);

    public static void main(String[] args) throws Exception {
        new MessageCollector().run(args);
    }

    @Override
    public void initialize(Bootstrap<MessageCollectorConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<MessageCollectorConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(MessageCollectorConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
        bootstrap.setName("message-collector");
    }

    @Override
    public void run(MessageCollectorConfiguration configuration,
                    Environment environment)
    throws ClassNotFoundException {

        //init jdbi
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "mysql");
        final MessageDAO messageDAO = jdbi.onDemand(MessageDAO.class);

        Scheduler scheduler = null;
        try {
             scheduler = StdSchedulerFactory.getDefaultScheduler();
         } catch (SchedulerException ex) {
             logger.info("unable to instantiate quartz scheduler.");
         }

        DataConverterJobFactory dataConverterJobFactory = new DataConverterJobFactory(messageDAO);

        environment.addResource(new SMSResponseResource());
        environment.manage(new QuartzManager(scheduler, configuration.getScheduledTaskConfiguration(), dataConverterJobFactory));
    }

}

