package fr.lernejo.navy_battle;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException, InterruptedException {
        try{
            final int port = Integer.parseInt(args[0]);
            if (args.length == 1) {
                System.out.println("listening on http://localhost:" + port + "/");
                new Server().start(port);
            } else if (args.length == 2) {
                new Server().start(port);
                new Client().start(port, args[1]);
            } else {
                throw new NumberFormatException("Too many arguments");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Port must be a number");
        }
    }
}
