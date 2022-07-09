package com.anas.discordbots.quranfm.listeners;

import com.anas.discordbots.quranfm.MainController;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.logging.Logger;

public class MessageListener implements MessageCreateListener {
    public static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(MessageListener.class.getName());
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        var message = event.getMessageContent();
        if (message.contains(" "))
            message = message.substring(0, message.indexOf(' ')); // Remove the command arguments
        switch (message.toLowerCase()) {
            case "!ping" -> event.getChannel().sendMessage("pong: " + event.getMessage().getCreationTimestamp());
            case "!join" -> {
                // Get the voice channel
                final var voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel();
                if (voiceChannel.isEmpty()) {
                    event.getChannel().sendMessage("You are not in a voice channel!");
                } else {
                    // Join the voice channel
                    final var fullMessage = event.getMessage().getContent();
                    try {
                        event.getChannel().sendMessage("Joining voice channel...");
                        LOGGER.info("Joining voice channel...");
                        MainController.getInstance().joinVoiceChannel(voiceChannel.get(),
                                fullMessage.contains(" ") ?
                                event.getMessageContent().substring("!join ".length()).split("\\s+") : null);
                    } catch (final IllegalStateException e) {
                        event.getChannel().sendMessage(e.getMessage());
                    }
                    LOGGER.fine("Joined voice channel!");
                }
            }
            case "!leave" -> {
                final var voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel();
                if (voiceChannel.isEmpty()) {
                    event.getChannel().sendMessage("You are not in a voice channel!");
                } else {
                    event.getChannel().sendMessage("Leaving voice channel...");
                    LOGGER.info("Leaving voice channel...");
                    MainController.getInstance().leaveVoiceChannel(voiceChannel.get());
                    LOGGER.fine("Left voice channel!");
                }
            }
            case "!surah" -> {
                final var voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel();
                if (voiceChannel.isEmpty()) {
                    event.getChannel().sendMessage("You are not in a voice channel!");
                    return;
                }
                final var  radio = MainController.getInstance().getQuranRadio(voiceChannel.get());
                if (radio == null) {
                    event.getChannel().sendMessage("There is no radio playing in this voice channel!");
                    return;
                }
                event.getChannel().sendMessage(radio.getCurrentSurah().getName());
            }
            case "!ayah" -> {
                final var voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel();
                if (voiceChannel.isEmpty()) {
                    event.getChannel().sendMessage("You are not in a voice channel!");
                    return;
                }
                final var radio = MainController.getInstance().getQuranRadio(voiceChannel.get());
                if (radio == null) {
                    event.getChannel().sendMessage("There is no radio playing in this voice channel!");
                    return;
                }
                event.getChannel().sendMessage(radio.getCurrentAyah().getText());
            }
            case "!ayah-number" -> {
                final var voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel();
                if (voiceChannel.isEmpty()) {
                    event.getChannel().sendMessage("You are not in a voice channel!");
                    return;
                }
                final var  radio = MainController.getInstance().getQuranRadio(voiceChannel.get());
                if (radio == null) {
                    event.getChannel().sendMessage("There is no radio playing in this voice channel!");
                    return;
                }
                event.getChannel().sendMessage(radio.getCurrentAyah().getNumberInSurah() + "");
            }
            case "!editions" -> {
                final var sb = new StringBuilder();
                for (final var edition : MainController.getInstance().getAvailableEditions()) {
                    sb.append(edition.getName()).append(" - ").append(edition.getIdentifier()).append("\n");
                }
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Available editions")
                        .setDescription(sb.toString()));
            }
            case "!developer" -> event.getChannel().sendMessage("""
                    **Anas Elgarhy**
                    **Discord:** Anas  Elgarhy#8946
                    **GitHub:** <https://github.com/anas-elgarhy>""");
            case "!help" -> {
                String sb = """
                        !ping - Ping the bot
                        !join - Join the voice channel and start playing the radio with the given editions or all
                        \b available editions if no editions are given
                        !leave - Leave the voice channel
                        !surah - Get the current surah
                        !ayah - Get the current ayah
                        !ayah-number - Get the current ayah number
                        !editions - Get the available editions
                        !developer - Get the developer information
                        !help - Get this help
                        """;
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Help")
                        .setDescription(sb));
            }
        }
    }
}
