package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class FireHandler implements HttpHandler {
    final private GameGrid gameGrid;

    public FireHandler(GameGrid gameGrid) {
        this.gameGrid = gameGrid;
    }

    public String getConsequence(int x, int y) {
        Ship ship = gameGrid.getGrid()[x][y];
        if (ship != null) {
            gameGrid.hitShip(x, y);
            if (ship.isAlive(gameGrid)) { return "hit"; }
            else { return "sunk"; }
        }
        gameGrid.colorMissedShip(x, y);
        return "missed";
    }
    public String constructBody(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String cell = query.substring(query.indexOf("cell=") + 5).trim();
        int x =  Integer.parseInt(cell.replace(Character.toString(cell.charAt(0)), "")) - 1;
        int y = cell.charAt(0) - 'A';
        String shipState; Boolean shipLeft;
        shipState = getConsequence(x, y);
        shipLeft = gameGrid.isShipLeft();
        return "{\"consequence\":\"" + shipState + "\",\"shipLeft\":" + shipLeft + "}";
    }

    public int parsePort(String query) {
        return Integer.parseInt(query.substring(query.indexOf("localhost:") + 10).trim());
    }

    public int parseAdversaryPort(String query) {
        return Integer.parseInt(query.substring(query.indexOf("localhost:") + 10).trim());
    }

    public void randomFire(int myPort, int adversaryPort) {
        Random random = new Random();
        char randomLetter = (char) (random.nextInt(10) + 'A'); int randomY = random.nextInt(10) + 1;
        String coordinates = randomLetter + Integer.toString(randomY);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + adversaryPort + "/api/game/fire?cell=" + coordinates))
            .setHeader("Accept", "application/json").setHeader("Content-Type", "application/json")
            .setHeader("X-Adversary-Port", Integer.toString(myPort))
            .setHeader("X-My-Port", Integer.toString(adversaryPort)).GET()
            .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }


    public void handle(HttpExchange exchange) throws IOException {
        String body; int adversaryPort = 0; int myPort = 0;
        try {
            if (exchange.getRequestHeaders().getFirst("X-Adversary-Port") != null) {
                adversaryPort = Integer.parseInt(exchange.getRequestHeaders().getFirst("X-Adversary-Port"));
            } else if (exchange.getRequestHeaders().getFirst("X-My-Port") != null) {
                myPort = Integer.parseInt(exchange.getRequestHeaders().getFirst("X-My-Port"));
            }
            body = constructBody(exchange);
            exchange.getResponseHeaders().set("Content-type", "application/json");
            exchange.sendResponseHeaders(202, body.length());
        } catch (Exception e) { body = "Error Bad Request"; exchange.sendResponseHeaders(400, body.length()); }
        Server server = new Server(); server.displayGrid(gameGrid);
        try (OutputStream os = exchange.getResponseBody()) { os.write(body.getBytes()); }
        while (gameGrid.isShipLeft()) {
            randomFire(myPort, adversaryPort);
            //try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
