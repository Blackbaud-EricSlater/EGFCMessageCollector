package com.blackbaud.messagecollector.job;

import com.blackbaud.messagecollector.dao.MessageDAO;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class DataConverterJobFactory implements JobFactory {

    private MessageDAO messageDAO;

    public DataConverterJobFactory(MessageDAO dao) {
        this.messageDAO = dao;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        return new DataConverterJob(messageDAO);
    }

}
