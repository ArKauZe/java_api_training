package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    public void Hit(GameGrid gameGrid, int i, int j) {
        System.out.print("\u001B[31m" + "X" + "\u001B[0m  |  ");
        gameGrid.getGrid()[i][j] = null;
    }

    public void Miss(GameGrid gameGrid, int i, int j) {
        System.out.print("\u001B[34m" + "0" + "\u001B[0m  |  ");
        gameGrid.getGrid()[i][j] = null;
    }

    public void displayGrid(GameGrid gameGrid) {
        System.out.print("New Round\n");
        System.out.print(String.format("\033[H\033[2J"));
        for (int i = 0 ; i < 10 ; i++) {
            for (int j = 0 ; j < 10 ; j++) {
                Ship element = gameGrid.getGrid()[i][j];
                if (element == null) {System.out.print(".  |  "); }
                else if (element.getSlug().equals("miss")) { Miss(gameGrid, i, j); }
                else if (element.getSlug().equals("hit")) { Hit(gameGrid, i, j); }
                else { System.out.print(element.getSlug().toUpperCase().charAt(0) + "  |  "); }
            }
            System.out.print("\n");
        }
    }

    public HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        GameGrid gameGrid = new GameGrid(10, 10);
        displayGrid(gameGrid);
        server.createContext("/ping", new PingHandler());
        server.createContext("/api/game/start", new StartHandler());
        server.createContext("/api/game/fire", new FireHandler(gameGrid));
        server.start();
        return server;
    }
}
