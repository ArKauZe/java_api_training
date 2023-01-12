package fr.lernejo.navy_battle;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        new Server().start(port);
    }
}
