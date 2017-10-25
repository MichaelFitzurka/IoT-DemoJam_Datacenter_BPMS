package com.redhat.demo.iotdemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpProducer {

    private final String USER_AGENT = "Mozilla/5.0";

    public HttpProducer() {
        super();
    }

    public void sendGet(final String httpURL) throws Exception {
        System.out.println("\nSending 'GET' request to URL : " + httpURL);

        URL url = new URL(httpURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = connection.getResponseCode();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = new String();
        StringBuilder response = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line + '\n');
        }
        bufferedReader.close();

        System.out.println("Executing get method returned value: " + response.toString());
        System.out.println("----------------------------------------");
        System.out.println(responseCode + " " + connection.getResponseMessage());
        System.out.println("----------------------------------------");

        connection.disconnect();
    }

    @Override
    public String toString() {
        return "HttpProducer []";
    }

}
