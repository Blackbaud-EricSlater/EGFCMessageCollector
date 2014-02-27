package com.blackbaud.messagecollector.quartz;

import com.blackbaud.messagecollector.job.TwilioScraperJob;
import com.blackbaud.messagecollector.job.TwilioScraperJobFactory;
import com.yammer.dropwizard.lifecycle.Managed;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blackbaud.messagecollector.configuration.ScheduledTaskConfiguration;

import java.util.concurrent.TimeUnit;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzManager implements Managed {

    private Scheduler scheduler;
    private Frequency jobFrequency;
    private TwilioScraperJobFactory twilioScraperJobFactory;

    final private Logger logger = LoggerFactory.getLogger(QuartzManager.class);

    public QuartzManager(Scheduler scheduler,
                         ScheduledTaskConfiguration config,
                         TwilioScraperJobFactory twilioScraperJobFactory) {
        this.scheduler = scheduler;
        this.jobFrequency = parseFrequency(config.getTwilioScraperJobFrequency());
        this.twilioScraperJobFactory = twilioScraperJobFactory;
    }

    @Override
    public void start() throws Exception {
        scheduler.start();
        buildPullFromTwilioJob();
    }

    @Override
    public void stop() throws Exception {
        scheduler.shutdown();
    }

    private Trigger buildTrigger(Frequency freq) {
        TriggerBuilder<Trigger> trigger = newTrigger();

        long freqAsMillis = TimeUnit.MILLISECONDS.convert(freq.getInterval(), freq.getTimeUnit());

        trigger.withSchedule(simpleSchedule()
               .withIntervalInMilliseconds(freqAsMillis)
               .repeatForever()).startNow();

        return trigger.build();
    }

    private void buildPullFromTwilioJob() throws SchedulerException {
        logger.info("Building CalculateAggregateFailureRateJob");

        scheduler.setJobFactory(twilioScraperJobFactory);

        //Do not schedule the job more than once
        String jobName;
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT"))) {
            jobName = jobKey.getName();
            if (jobName.equals("TwilioScraperJob"))
                return;
        }

        JobDetail job = newJob(TwilioScraperJob.class).withIdentity("TwilioScraperJob").build();
        Trigger trigger = buildTrigger(jobFrequency);
        scheduler.scheduleJob(job, trigger);

        logger.info("Scheduled CalculateAggregateFailureRate with frequency " +
                jobFrequency.getInterval() + " " +
                jobFrequency.getTimeUnit());
    }

    private Frequency parseFrequency(String frequency) {
        if (null == frequency)
            return new Frequency(1, TimeUnit.HOURS);

        if (frequency.matches("[1-9]+[0-9]*-[mMhHdD]")) {
            String[] tokens = frequency.split("-");
            return new Frequency(tokens[0], tokens[1]);
        } else
            return new Frequency(1, TimeUnit.HOURS);
    }

    private class Frequency {

        private int interval;
        private TimeUnit timeUnit;

        public Frequency(String interval, String timeUnit) {
            this.interval = castInterval(interval);
            this.timeUnit = castTimeUnit(timeUnit);
        }

        public Frequency(int interval, TimeUnit timeUnit) {
            this.interval = interval;
            this.timeUnit = timeUnit;
        }

        public int getInterval() {
            return interval;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        private int castInterval(String interval) {
            int intervalAsInt = 1;
            try {
                intervalAsInt = Integer.parseInt(interval);
            } catch (Exception ex) {
                logger.debug("Unable to cast interval. Using default of 1.");
            }
            return intervalAsInt;
        }

        private TimeUnit castTimeUnit(String timeUnit) {
            TimeUnit castedTimeUnit;
            switch (timeUnit) {
                //Days
                case "d":
                case "D":
                    castedTimeUnit = TimeUnit.DAYS;
                    break;

                //Hours
                case "h":
                case "H":
                    castedTimeUnit = TimeUnit.HOURS;
                    break;

                //Minutes
                case "m":
                case "M":
                    castedTimeUnit = TimeUnit.MINUTES;
                    break;

                default:
                    castedTimeUnit = TimeUnit.HOURS;
                    logger.debug("Unable to cast timeUnit. Using default of HOUR.");
                    break;

            }
            return castedTimeUnit;
        }
    }
}
