package com.blackbaud.messagecollector.job;


import com.blackbaud.messagecollector.core.SurveyResponse;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.MessageList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.ajax.JSON;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataConverterJob implements Job {

    final private Logger logger = LoggerFactory.getLogger(DataConverterJob.class);

    public static final String ACCOUNT_SID = "ACdb24e1e8078283f9f7ce1cf464f124c7";
    public static final String AUTH_TOKEN = "2e9cf192efa44c678fe91c6ea9430e1b";

    private static Map<String, String> SHORT_NAME_TO_LONG_NAME = new HashMap<>();
    static {
        SHORT_NAME_TO_LONG_NAME.put("AARP", "American Association of Retired Persons");
        SHORT_NAME_TO_LONG_NAME.put("EDF", "Environmental Defense Fund");
        SHORT_NAME_TO_LONG_NAME.put("JDRF", "Juvenile Diabetes Research Foundation");
        SHORT_NAME_TO_LONG_NAME.put("LCV", "League of Conservation Voters");
        SHORT_NAME_TO_LONG_NAME.put("MJFF", "Michael J. Fox Foundation");
        SHORT_NAME_TO_LONG_NAME.put("LIVESTRONG", "Livestrong");
        SHORT_NAME_TO_LONG_NAME.put("KOMEN", "Susan G. Komen");
    }

    public static final String INBOUND_REPLY = "inbound";

    public void execute(JobExecutionContext context) throws JobExecutionException {

        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        MessageList messages = client.getAccount().getMessages();

        List<SurveyResponse> surveyResponseList = createSurveyResponseList(messages);

        String authToken = null;
        try {
            authToken = authenticateWithSalesforce();
        } catch (Exception ex) {
            logger.error("salesforce auth failed!!!!!!",ex.toString());
        }

        try {
            if (authToken != null) {
                pushSurveyResponsesToSalesforce(authToken, surveyResponseList);
            } else {
                logger.error("no data push. auth token invalid!!!!!");
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

    }

    private List<SurveyResponse> createSurveyResponseList(MessageList messageList) {
        List<SurveyResponse> surveyResponseList = new ArrayList<>();
        for (Message m : messageList) {
            if (m.getDirection().equals(INBOUND_REPLY)) {
                logger.debug("made list entry to send to salesforce");
                Map.Entry<String,String> names = parseNameFromMessage(m.getBody());
                if(names != null) {
                    surveyResponseList.add(new SurveyResponse(m.getFrom(),
                                                              m.getDateSent(),
                                                              names.getValue(),
                                                              names.getKey(),
                                                              m.getSid(),
                                                              "005F0000003VVjD"));
                }
            }
        }
        return surveyResponseList;
    }

    private Map.Entry<String,String> parseNameFromMessage(String body) {
        logger.info("Parsing SMS message with contents: "  + body);
        for (Map.Entry e : SHORT_NAME_TO_LONG_NAME.entrySet()) {
            Matcher m = Pattern.compile(Pattern.quote((String)e.getKey()), Pattern.CASE_INSENSITIVE).matcher(body);
            if(m.find()) {
                logger.info("Done Parsing. Match.");
                return e;
            }
            m = Pattern.compile(Pattern.quote((String)e.getValue()), Pattern.CASE_INSENSITIVE).matcher(body);
            if(m.find()) {
                logger.info("Done Parsing. Match.");
                return e;
            }
        }
        logger.info("Done Parsing. No Match Found.");
        return null;
    }

    private String authenticateWithSalesforce()
        throws UnsupportedEncodingException, ClientProtocolException, IOException {

        String username = "cbrooking+heroku@convio.com";
        String loginHost = "https://login.salesforce.com";
        String password="Password0x1DXai8sAprHH2Fwq4dfgffdr";
        String clientId= "3MVG9JZ_r.QzrS7jMWJX1xnsIjZxmGcCpiHbFFLN0pOAtf6W0Q.9.8.O9ejAt3Fjz7Di8xnzWlePkmkgvb7I2";
        String clientSecret="5922702105060123927";

        // Set up an HTTP client that makes a connection to REST API.
        DefaultHttpClient client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);
        params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30000);

        // Set the SID.
        logger.info("Logging in as " + username + " in environment " + loginHost);
        String baseUrl = loginHost + "/services/oauth2/token";

        // Send a post request to the OAuth URL.
        HttpPost oauthPost = new HttpPost(baseUrl);

        // The request body must contain these 5 values.
        List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
        parametersBody.add(new BasicNameValuePair("grant_type", "password"));
        parametersBody.add(new BasicNameValuePair("username", username));
        parametersBody.add(new BasicNameValuePair("password", password));
        parametersBody.add(new BasicNameValuePair("client_id", clientId));
        parametersBody.add(new BasicNameValuePair("client_secret", clientSecret));
        oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));

        // Execute the request.
        logger.debug("POST " + baseUrl + "...\n");
        HttpResponse response = client.execute(oauthPost);
        int code = response.getStatusLine().getStatusCode();
        Map<String, String> oauthLoginResponse = (Map<String, String>)
                JSON.parse(EntityUtils.toString(response.getEntity()));
        logger.debug("OAuth login response");
        for (Map.Entry<String, String> entry : oauthLoginResponse.entrySet())
        {
            logger.debug(String.format("  %s = %s", entry.getKey(), entry.getValue()));
        }
        logger.info("login complete");

        return oauthLoginResponse.get("access_token");
    }

    private void pushSurveyResponsesToSalesforce(String authToken, List<SurveyResponse> surveyResponseList)
        throws Exception {

        String surveyResponseUrl = "https://na10.salesforce.com/services/data/v20.0/sobjects/Survey_Response__c/";

        for (SurveyResponse sr : surveyResponseList) {

            HttpPost httpPost = new HttpPost(surveyResponseUrl);

            ObjectMapper mapper = new ObjectMapper();
            String asJson = mapper.writeValueAsString(sr);

            logger.info("Sending JSON: " + asJson);

            String token = "Bearer " + authToken;

            httpPost.setHeader("Authorization", token);
            httpPost.setHeader("X-PrettyPrint", "1");

            StringEntity entity = new StringEntity(asJson, HTTP.UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            HttpClient client = new DefaultHttpClient();

            HttpResponse response = client.execute(httpPost);
            String responseEntity = EntityUtils.toString(response.getEntity());

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 200 || responseCode == 201)
                logger.info("New entry successfully created");
            else if (responseCode == 400 && responseEntity.contains("DUPLICATE_VALUE"))
                logger.info("Duplicate Submitted");
            else
                logger.info("Salesforce rejected submission!! Code: " + responseCode + " " + responseEntity);
        }
    }
}

