package org.adarssh;

import java.util.Scanner;

import static org.adarssh.SpotifyCredentialHandling.obtainToken;

public class MainApp {

    private static final TopSpotifySongs topSpotifySongs = new TopSpotifySongs();

    private static final TopSpotifyArtists topSpotifyArtists = new TopSpotifyArtists();

    private static final TopSpotifyAlbums topSpotifyAlbums = new TopSpotifyAlbums();

    private static final TopSpotifyGenres topSpotifyGenres = new TopSpotifyGenres();

    private static String accessToken;
    public static void main(String[] args) {
//        getUserAccessToken();
        accessToken = obtainToken();
        System.out.println();
        topSpotifySongs.outputTopSpotifySongs(accessToken);
        System.out.println();
        topSpotifyArtists.outputTopSpotifyArtists(accessToken);
        System.out.println();
        topSpotifyAlbums.outputTopSpotifyAlbums(accessToken);
        System.out.println();
        topSpotifyGenres.outputTopSpotifyGenres(accessToken);

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
