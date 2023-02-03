package org.adarssh;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class TopSpotifyGenres extends SpotifyCrdentialHandling {

    private static final String API_URL = "https://api.spotify.com/v1/me/top/artists?limit=5";

    public void outputTopSpotifyGenres(String ACCESS_TOKEN) {
        try {
            HttpURLConnection connection = getHttpURLConnection(ACCESS_TOKEN,API_URL);

            int status = connection.getResponseCode();
            if (status == 200) {
                JSONArray items = reader(connection);
                System.out.println("My top Spotify genres of the month are:");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONArray genres = item.getJSONArray("genres");
                    for (int j = 0; j < genres.length(); j++) {
                        String genre = genres.getString(j);
                        System.out.println(genre);
                    }
                }
            } else {
                System.out.println("Failed to retrieve top Spotify genres: " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
