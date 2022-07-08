package com.anas.discordbots.quranfm;


import com.anas.discordbots.quranfm.player.LavaplayerAudioSource;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;


public class QuranLavaplayerAudio {

    private final AudioPlayerManager audioPlayerManager;
    private final AudioPlayer audioPlayer;
    private final AudioSource audioSource;
    /**
     * Creates a new lavaplayer audio source.
     *
     * @param api A discord api instance.
     */
    public QuranLavaplayerAudio(final DiscordApi api, final AudioConnection audioConnection) {
        audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.audioSource = new LavaplayerAudioSource(api, audioPlayer);
        audioConnection.setAudioSource(audioSource);
        audioPlayerManager.loadItem("", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(final AudioTrack track) {
                audioPlayer.playTrack(track);
            }

            @Override
            public void playlistLoaded(final AudioPlaylist playlist) {
                for (var track : playlist.getTracks()) {
                    audioPlayer.playTrack(track);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(final FriendlyException exception) {

            }
        });
    }
}
