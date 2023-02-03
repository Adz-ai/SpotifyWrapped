package org.adarssh;

import java.util.Scanner;

public class MainApp {

    private static final TopSpotifySongs topSpotifySongs = new TopSpotifySongs();

    private static final TopSpotifyArtists topSpotifyArtists = new TopSpotifyArtists();

    private static String accessToken;
    public static void main(String[] args) {
        getUserAccessToken();
        System.out.println();
        topSpotifySongs.outputTopSpotifySongs(accessToken);
        System.out.println();
        topSpotifyArtists.outputTopSpotifyArtists(accessToken);

    }

    private static void getUserAccessToken() {
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter Spotify Access Token");
        setAccessToken(myObj.nextLine());
    }

    private static void setAccessToken(String token) {
        accessToken = token;
    }
}
