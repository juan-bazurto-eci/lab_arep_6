package co.edu.escuelaing.arep.roundrobin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRemoteCaller {

    private static final String USER_AGENT = "Mozilla/5.0 Chrome/51.0.2704.103 Safari/537.36";
    private static String GET_URL = "";

    private static String Method = "";

    public static String doRequestGet() throws IOException {
        System.out.println("REGUEST SENDED TO LOG SERVICE" + getGetUrl() + getMethod());
        URL obj = new URL(getGetUrl());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(getMethod());
        System.out.println(getGetUrl() + " " + getMethod());
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println("ANSWER FROM LOG SERVICE IN ROUND ROBIN" + response.toString());
            return response.toString();
        } else {
            System.out.println("request not worked");
            return "request not worked";
        }
    }

    public static String doRequestPost(String data) throws IOException {
        System.out.println("REGUEST SENDED TO LOG SERVICE " + getGetUrl() + " " + getMethod());
        URL obj = new URL(getGetUrl());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(getMethod());
        con.setDoOutput(true);
        System.out.println(getGetUrl() + " " + getMethod());
        con.setRequestProperty("User-Agent", USER_AGENT);
        int length = data.length();

        con.setFixedLengthStreamingMode(length);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.connect();
        try(OutputStream os = con.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println("ANSWER FROM LOG SERVICE IN ROUND ROBIN " + response.toString());
            return response.toString();
        } else {
            System.out.println("request not worked");
            return "request not worked";
        }
    }
    public static String getGetUrl() {
        return GET_URL;
    }

    public static void setGetUrl(String getUrl) {
        GET_URL = getUrl;
    }

    public static String getMethod() {
        return Method;
    }

    public static void setMethod(String method) {
        Method = method;
    }

}

