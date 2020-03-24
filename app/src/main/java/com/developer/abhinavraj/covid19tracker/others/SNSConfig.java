package com.developer.abhinavraj.covid19tracker.others;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.DynamoDBEntry;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SNSConfig {

    private static String filename = "ARN";
    AmazonSNSClient snsClient;
    String arnStorage = null;
    Context ctx;
    JSONObject jsonObject;
    String accessKey, secretKey, endpoint;
    AWSCredentials awsCredentials;

    public SNSConfig(Context ctx) {
        this.ctx = ctx;

        try {
            jsonObject = new JSONObject(Utility.makeApiCallAndGetResponseForAws("data","key"));

            // Access Key
            accessKey = jsonObject.getString("accessKey");
            accessKey = accessKey.substring(1);
            accessKey = accessKey.substring(0, accessKey.length()-1);

            // Secret Key
            secretKey = jsonObject.getString("secretKey");
            secretKey = secretKey.substring(1);
            secretKey = secretKey.substring(0, secretKey.length()-1);

            // ARN
            endpoint = jsonObject.getString("endpoint");
            endpoint = endpoint.substring(1);
            endpoint = endpoint.substring(0, endpoint.length()-1);

        } catch (Exception e) {
            //pass
        }

        try {
            awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            snsClient = new AmazonSNSClient(awsCredentials);
            snsClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        } catch (Exception ex) {
            Log.e("AWS_CREDENTIALS",ex.getMessage());
        }

        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            String endpointArn = retrieveEndpointArn();

            boolean updateNeeded = false;
            boolean createNeeded = (null == endpointArn);

            if (createNeeded) {
                new RegisterSNS().execute(refreshedToken);
            } else {
                try {
                    GetEndpointAttributesRequest geaReq =
                            new GetEndpointAttributesRequest()
                                    .withEndpointArn(endpointArn);
                    GetEndpointAttributesResult geaRes =
                            snsClient.getEndpointAttributes(geaReq);

                    updateNeeded = !geaRes.getAttributes().get("Token").equals(refreshedToken)
                            || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");

                } catch (Exception e) {
                    createNeeded = true;
                }

                if (createNeeded) {
                    new RegisterSNS().execute(refreshedToken);
                } else {
                    if (updateNeeded) {
                        Map attribs = new HashMap();
                        attribs.put("Token", refreshedToken);
                        attribs.put("Enabled", "true");
                        SetEndpointAttributesRequest saeReq =
                                new SetEndpointAttributesRequest()
                                        .withEndpointArn(endpointArn)
                                        .withAttributes(attribs);
                        snsClient.setEndpointAttributes(saeReq);
                    }
                }
            }

        } catch (Exception e) {
            Log.e("Token", "Can't get refreshed token");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RegisterSNS extends AsyncTask<String,Void,Void> {
        String endpointArn = null;
        boolean updateNeeded = false;
        String token;

        @Override
        protected Void doInBackground(String... strings) {
           token = strings[0];
            try {
                CreatePlatformEndpointRequest cpeReq =
                        new CreatePlatformEndpointRequest()
                                .withPlatformApplicationArn(endpoint)
                                .withToken(strings[0]);
                CreatePlatformEndpointResult cpeRes = snsClient.createPlatformEndpoint(cpeReq);
                endpointArn = cpeRes.getEndpointArn();
            } catch (InvalidParameterException ipe) {
                String message = ipe.getErrorMessage();
                Pattern p = Pattern
                        .compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " +
                                "with the same [Tt]oken.*");
                Matcher m = p.matcher(message);
                if (m.matches()) {
                    endpointArn = m.group(1);
                } else {
                    throw ipe;
                }
            } catch (Exception e) {
                // pass
            }

            if (endpointArn != null && !endpointArn.equals("")) {

                // Push ARN on Dynamo DB Table
                try {
                    // Config
                    AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(awsCredentials);
                    dbClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));

                    // Get subscribers current count
                    Table countTable = Table.loadTable(dbClient, "corona_count_for_notifications");
                    Document subscribersCount = countTable.getItem(new Primitive("subscribers_count"));
                    DynamoDBEntry value = subscribersCount.get("props_value");
                    String count = value.asString();
                    // Log.d("VALUE", count);

                    // Add to subscribers list
                    //Table subscribersTable = Table.loadTable(dbClient, "corona_notifications_subscribers");
                    Table subscribersTable = Table.loadTable(dbClient, "corona_notifications_subs");
                    Document newSubscriber = new Document();
                    // newSubscriber.put("id", Integer.parseInt(count)+1);
                    newSubscriber.put("arn", endpointArn);
                    subscribersTable.putItem(newSubscriber);

                    // Update subscribers count
                    HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
                    key.put("props", new AttributeValue().withS("subscribers_count"));

                    Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
                    expressionAttributeValues.put(":val1", new AttributeValue().withS(String.valueOf(Integer.parseInt(count) + 1)));

                    UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                            .withTableName("corona_count_for_notifications")
                            .withKey(key)
                            .withUpdateExpression("set props_value = :val1")
                            .withExpressionAttributeValues(expressionAttributeValues)
                            .withReturnValues(ReturnValue.ALL_NEW);
                    dbClient.updateItem(updateItemRequest);

                    Log.d("Update", "Completed");

                    storeEndpointArn(endpointArn);

                } catch (Exception e) {
                    Log.e("Dynamo DB", e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                GetEndpointAttributesRequest geaReq =
                        new GetEndpointAttributesRequest()
                                .withEndpointArn(endpointArn);
                GetEndpointAttributesResult geaRes =
                        snsClient.getEndpointAttributes(geaReq);

                updateNeeded = !geaRes.getAttributes().get("Token").equals(token)
                        || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");

            } catch (Exception e) {
                // createNeeded = true;
            }

            if(updateNeeded) {
                Map attribs = new HashMap();
                attribs.put("Token", token);
                attribs.put("Enabled", "true");
                SetEndpointAttributesRequest saeReq =
                        new SetEndpointAttributesRequest()
                                .withEndpointArn(endpointArn)
                                .withAttributes(attribs);
                snsClient.setEndpointAttributes(saeReq);
            }
        }
    }

    private String retrieveEndpointArn() {
        StringBuilder temp = new StringBuilder();

        try {
            FileInputStream fin = ctx.openFileInput(filename);
            int c;
            while ((c = fin.read()) != -1) {
                temp.append((char) c);
            }
        } catch (Exception e) {
            // No data found
        }

        if (temp.toString().equals(""))
            arnStorage = null;
        else
            arnStorage = temp.toString();

        return arnStorage;
    }


    private void storeEndpointArn(String endpointArn) {

        FileOutputStream fos;

        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(endpointArn.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        arnStorage = endpointArn;
    }
}
