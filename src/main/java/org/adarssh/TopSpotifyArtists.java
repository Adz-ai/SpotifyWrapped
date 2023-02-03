package org.adarssh;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class TopSpotifyArtists extends SpotifyCredentialHandling {

    private static final String API_URL = "https://api.spotify.com/v1/me/top/artists?limit=5";

    public void outputTopSpotifyArtists(String ACCESS_TOKEN) {
        try {
            HttpURLConnection connection = getHttpURLConnection(ACCESS_TOKEN,API_URL);

            int status = connection.getResponseCode();
            if (status == 200) {
                JSONArray items = reader(connection);
                System.out.println("My top Spotify artists of the month are:");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String name = item.getString("name");
                    System.out.println(name);
                }
            } else {
                System.out.println("Failed to retrieve top Spotify artists: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
