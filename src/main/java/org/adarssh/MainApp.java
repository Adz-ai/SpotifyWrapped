package org.adarssh;

public class MainApp {

    private static final TopSpotifySongs topSpotifySongs = new TopSpotifySongs();

    private static final TopSpotifyArtists topSpotifyArtists = new TopSpotifyArtists();

    private static final String ACCESS_TOKEN = "INSERT_ACCESS_TOKEN";
    public static void main(String[] args) {
        System.out.println();
        topSpotifySongs.outputTopSpotifySongs(ACCESS_TOKEN);
        System.out.println();
        topSpotifyArtists.outputTopSpotifyArtists(ACCESS_TOKEN);

    }
}
