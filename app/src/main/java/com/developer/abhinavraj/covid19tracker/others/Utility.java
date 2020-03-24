package com.developer.abhinavraj.covid19tracker.others;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.core.view.ViewCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

public class Utility {

    public static String makeApiCallAndGetResponse(String param1, String param2) {

        String result = "NONE";

        try {
            JSONObject out = new JSONObject();
            out.put(param1, param2);

            URL obj = new URL("https://174y6n1sy5.execute-api.ap-south-1.amazonaws.com/prod");
            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(postConnection.getOutputStream());
            os.write(out.toString());
            os.flush();
            os.close();
            int responseCode = postConnection.getResponseCode();
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        postConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                Log.e("HTTP Connection","POST NOT WORKED");
                return result;
            }

        } catch (Exception ex) {
            Log.e("Async", ex.getMessage());
        }

        return result;
    }

    public static String makeApiCallAndGetResponseForAws(String param1, String param2) {

        String result = "NONE";

        try {
            JSONObject out = new JSONObject();
            out.put(param1, param2);

            URL obj = new URL("https://3ytq3ya6l1.execute-api.ap-south-1.amazonaws.com/prod");
            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
            postConnection.setRequestMethod("POST");
            postConnection.setDoOutput(true);
            OutputStreamWriter os = new OutputStreamWriter(postConnection.getOutputStream());
            os.write(out.toString());
            os.flush();
            os.close();
            int responseCode = postConnection.getResponseCode();
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        postConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
                return response.toString();
            } else {
                System.out.println("POST NOT WORKED");
                return result;
            }

        } catch (Exception ex) {
            Log.e("Async", ex.getMessage());
        }

        return result;
    }

    public static String readJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String formatNumber(int number) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
    }

    public static void setTranslationZ(View view, float translationZ) {
        ViewCompat.setTranslationZ(view, translationZ);
    }
}
