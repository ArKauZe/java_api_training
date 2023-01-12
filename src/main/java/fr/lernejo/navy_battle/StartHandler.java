package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.util.UUID;
import java.util.stream.Collectors;

public class StartHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("POST")) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String requestBody = reader.lines().collect(Collectors.joining());
                JSONObject json = new JSONObject(requestBody);
                JSONObject responseJson = new JSONObject();
                responseJson.put("id", UUID.randomUUID().toString());
                responseJson.put("url", "http://localhost:" + exchange.getLocalAddress().getPort());
                responseJson.put("message", "May the best code win");
                exchange.sendResponseHeaders(202, responseJson.toString().length());
                try (OutputStream os = exchange.getResponseBody()) { os.write(responseJson.toString().getBytes()); }
            } catch (Exception e) { exchange.sendResponseHeaders(400, -1); }
        } else { exchange.sendResponseHeaders(404, -1); }
    }
}
