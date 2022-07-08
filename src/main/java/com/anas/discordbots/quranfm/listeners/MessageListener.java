package com.anas.discordbots.quranfm.listeners;

import com.anas.discordbots.quranfm.MainController;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Arrays;
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
                    event.getChannel().sendMessage("Joining voice channel...");
                    LOGGER.info("Joining voice channel...");
                    final var fullMessage = event.getMessage().getContent();
                    MainController.getInstance().joinVoiceChannel(voiceChannel.get(), fullMessage.contains(" ") ?
                            event.getMessageContent().substring("!join ".length()).split("\\s+") : null);
                    LOGGER.fine("Joined voice channel!");
                }
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
        }
    }
}
