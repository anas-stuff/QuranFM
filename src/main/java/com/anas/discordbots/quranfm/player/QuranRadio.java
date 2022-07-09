package com.anas.discordbots.quranfm.player;


import com.anas.alqurancloudapi.QuranAPI;
import com.anas.alqurancloudapi.consts.Surahs;
import com.anas.alqurancloudapi.quran.Ayah;
import com.anas.alqurancloudapi.quran.Surah;
import com.anas.alqurancloudapi.quran.edition.Edition;
import com.anas.discordbots.quranfm.MainController;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import org.javacord.api.audio.AudioConnection;

import java.io.IOException;
import java.util.logging.Logger;


public class QuranRadio implements Runnable {
    private final static Logger LOGGER;
    private AudioPlayerManager playerManager;
    private AudioPlayer audioPlayer;
    private final Edition[] editions;
    private Surah currentSurah;
    private Ayah currentAyah;

    static {
        LOGGER = Logger.getLogger(QuranRadio.class.getName());
    }

    public QuranRadio(final AudioConnection audioConnection, final Edition[] editions) {
        this.editions = editions;
        currentSurah = null;
        currentAyah = null;
        setup(audioConnection);
    }

    private void setup(final AudioConnection audioConnection) {
        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        this.audioPlayer = playerManager.createPlayer();
        audioConnection.setAudioSource(new LavaplayerAudioSource(MainController.getInstance().getDiscordApi(), audioPlayer));
        audioPlayer.addListener(new AudioPlayerListener());
    }

    @Override
    public void run() {
        if (currentSurah == null || (currentAyah != null &&
                currentSurah.getAyahs().length - 1 == currentAyah.getNumberInSurah())) {
            try {
                currentSurah = QuranAPI.getSurah(Surahs.values()[(int) (Math.random() * Surahs.values().length)],
                        editions[(int) (Math.random() * editions.length)]);
                currentAyah = null;
                LOGGER.info("Changed surah to " + currentSurah.getName());
            } catch (IOException e) {
                LOGGER.severe("Error while getting surah: " + e.getMessage());
                return;
            }
        }
        if (currentAyah == null) {
            currentAyah = currentSurah.getAyahs()[0];
        } else {
            currentAyah = currentSurah.getAyahs()[currentAyah.getNumberInSurah()];
        }
        playerManager.loadItem(currentAyah.getAudioUrl(), new ResultHandler(audioPlayer));
        LOGGER.info("Loading surah " + currentSurah.getName() + " ayah " + currentAyah.getNumberInSurah());
    }

    private class AudioPlayerListener implements AudioEventListener {
        @Override
        public void onEvent(AudioEvent event) {
            System.out.println("Is paused: " + event.player.isPaused());
            if (event.player.getPlayingTrack() == null) {
                run();
            }
        }
    }
}
