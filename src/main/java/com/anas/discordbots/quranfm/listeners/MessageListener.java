package com.anas.discordbots.quranfm.listeners;

import com.anas.discordbots.quranfm.MainController;
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
        final var message = event.getMessageContent();
        if (message.equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("pong: " + event.getMessage().getCreationTimestamp());
        } else if (message.equalsIgnoreCase("!join")) {
            // Get the voice channel
            final var voiceChannel = event.getMessageAuthor().getConnectedVoiceChannel();
            if (voiceChannel.isEmpty()) {
                event.getChannel().sendMessage("You are not in a voice channel!");
            } else {
                // Join the voice channel
                event.getChannel().sendMessage("Joining voice channel...");
                LOGGER.info("Joining voice channel...");
                MainController.getInstance().joinVoiceChannel(voiceChannel.get());
                LOGGER.fine("Joined voice channel!");
            }
        }
    }
}
