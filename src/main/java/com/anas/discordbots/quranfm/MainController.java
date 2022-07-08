package com.anas.discordbots.quranfm;

import com.anas.alqurancloudapi.QuranAPI;
import com.anas.alqurancloudapi.quran.edition.Edition;
import com.anas.alqurancloudapi.quran.edition.EditionFormat;
import com.anas.discordbots.quranfm.listeners.MessageListener;
import com.anas.discordbots.quranfm.listeners.SlashCommandsListener;
import com.anas.discordbots.quranfm.player.QuranRadio;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.io.IOException;
import java.util.ArrayList;
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
        LOGGER.fine("Bot is ready!");
        LOGGER.info("Invite link: " + discordApi.createBotInvite());
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
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "editions",
                                        "The edition of quran to join (default: all)", false))),
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

    public void joinVoiceChannel(final ServerVoiceChannel serverVoiceChannel, final String[] editions) {
        serverVoiceChannel.connect().thenAccept(audioConnection -> {
            LOGGER.info("Connected to voice channel!");
            new Thread(new QuranRadio(audioConnection, getEdititons(editions))).start();
        }).exceptionally(throwable -> {
            LOGGER.severe("Failed to connect to voice channel: " + throwable.getMessage());
            return null;
        });
    }

    private Edition[] getEdititons(final String[] editions) {
        if (editions == null || editions.length == 0) {
            return availableEditions;
        }
        final var selectedEditions = new ArrayList<Edition>();
        for (final String s : editions) {
            for (final Edition edition : availableEditions) {
                if (edition.getName().equals(s) || edition.getIdentifier().equals(s)) {
                    selectedEditions.add(edition);
                    break;
                }
            }
        }
        return selectedEditions.toArray(new Edition[0]);
    }

    public DiscordApi getDiscordApi() {
        return discordApi;
    }

    public Edition[] getAvailableEditions() {
        return availableEditions;
    }
}
