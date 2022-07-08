package com.anas.discordbots.quranfm;

public class Main {
    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar quranfm.jar <bot token>");
            System.exit(1);
        }
        MainController.getInstance().start(args[0]);
    }
}
