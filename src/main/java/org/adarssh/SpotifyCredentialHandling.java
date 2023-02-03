package org.adarssh;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SpotifyCredentialHandling {
    private static final String API_URL = "https://accounts.spotify.com/api/token";
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";


    public static JSONArray reader(HttpURLConnection connection) throws IOException {
        JSONObject response = outputResponse(connection);
        return response.getJSONArray("items");
    }


    public static HttpURLConnection getHttpURLConnection(String ACCESS_TOKEN, String API_URL) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        connection.setRequestProperty("Accept", "application/json");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        return connection;
    }

    public static String obtainToken() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("grant_type", "client_credentials");
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                if (postData.length() != 0) {
                    postData.append("&");
                }
                postData.append(param.getKey());
                postData.append("=");
                postData.append(param.getValue());
            }
            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
            connection.getOutputStream().write(postDataBytes);

            int status = connection.getResponseCode();
            if (status == 200) {
                JSONObject response = outputResponse(connection);
                return response.getString("access_token");
            } else {
                System.out.println("Failed to obtain Spotify API access token: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject outputResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return new JSONObject(builder.toString());
    }
}
