package com.blackbaud.messagecollector.job;


import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.MessageList;
import org.apache.http.NameValuePair;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TwilioScraperJob implements Job {

    final private Logger logger = LoggerFactory.getLogger(TwilioScraperJob.class);

    public static final String ACCOUNT_SID = "ACdb24e1e8078283f9f7ce1cf464f124c7";
    public static final String AUTH_TOKEN = "2e9cf192efa44c678fe91c6ea9430e1b";

    public static final String OUTBOUND_REPLY = "outbound-reply";

    public void execute(JobExecutionContext context) throws JobExecutionException {

        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        MessageList messages = client.getAccount().getMessages();

        List<Message> outbound = new ArrayList<Message>();

        for (Message message : messages) {
            logger.debug(message.getDirection());
            logger.debug(message.getDateSent().toString());
            logger.debug(message.getFrom());
            logger.debug(message.getTo());
            logger.debug(message.getBody());
        }
    }

}
