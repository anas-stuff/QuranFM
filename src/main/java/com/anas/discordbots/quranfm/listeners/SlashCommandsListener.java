package com.anas.discordbots.quranfm.listeners;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.logging.Logger;

public class SlashCommandsListener implements SlashCommandCreateListener {
    private final static Logger LOGGER;

    static {
        LOGGER = Logger.getLogger(SlashCommandsListener.class.getName());
    }


    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {

    }
}
