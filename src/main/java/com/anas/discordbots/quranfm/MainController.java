package com.anas.discordbots.quranfm;

import com.anas.alqurancloudapi.QuranAPI;
import com.anas.alqurancloudapi.quran.edition.Edition;
import com.anas.alqurancloudapi.quran.edition.EditionFormat;
import com.anas.discordbots.quranfm.listeners.MessageListener;
import com.anas.discordbots.quranfm.listeners.SlashCommandsListener;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

public class MainController {
    private Edition[] availableEditions;
    private DiscordApi discordApi;
    private static final Logger LOGGER;
    private static MainController instance;

    static {
        LOGGER = Logger.getLogger(MainController.class.getName());
    }

    private MainController() {
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    public void start(final String token) {
        init(token);
        setup();
    }

    private void setup() {
        // Setup bot activity
        discordApi.updateActivity("Listening to the Quran 📻");
        // Setup bot commands (Slash commands)
        setupSlashCommands();
        discordApi.addSlashCommandCreateListener(new SlashCommandsListener());
    }

    private void setupSlashCommands() {
        LOGGER.info("Setting up slash commands...");
        discordApi.bulkOverwriteGlobalApplicationCommands(Arrays.asList(
                new SlashCommandBuilder().setName("join").setDescription("Join the voice channel")
                        .setOptions(Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "edition",
                                        "The edition of quran to join (default: " +
                                                availableEditions[0].getName() + ")", false))),
                new SlashCommandBuilder().setName("leave").setDescription("Leave the voice channel"),
                new SlashCommandBuilder().setName("surah").setDescription("Get the current surah name"),
                new SlashCommandBuilder().setName("ayah").setDescription("Get the current ayah"),
                new SlashCommandBuilder().setName("ayahnumber").setDescription("Get the current ayah number"),
                new SlashCommandBuilder().setName("ping").setDescription("Get the bot ping"),
                new SlashCommandBuilder().setName("help").setDescription("Get the help message"),
                new SlashCommandBuilder().setName("editions").setDescription("Get the available editions")
        )).join();
        LOGGER.info("Slash commands setup complete.");
    }

    private void init(final String token) {
        LOGGER.info("Initializing...");
        try {
            availableEditions = QuranAPI.getEditions(EditionFormat.AUDIO);
            discordApi = new DiscordApiBuilder().setToken(token)
                    .addMessageCreateListener(new MessageListener()).login().join();
        } catch (IOException | CancellationException | CompletionException e) {
            LOGGER.severe("Failed to initialize: " + e.getMessage());
            System.exit(1);
        }
    }

    public DiscordApi getDiscordApi() {
        return discordApi;
    }

    public void joinVoiceChannel(final ServerVoiceChannel serverVoiceChannel) {
        serverVoiceChannel.connect().thenAccept(audioConnection -> {
            LOGGER.info("Connected to voice channel!");

        }).exceptionally(throwable -> {
            LOGGER.severe("Failed to connect to voice channel: " + throwable.getMessage());
            return null;
        });
    }
}
