package fr.lernejo.navy_battle;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;

public class ClientTest {
    @Test
    public void startTest() throws IOException, InterruptedException {
        int port = 6666;
        Server server = new Server();
        server.start(port);
        Client client = new Client();
        client.start(7777, "http://localhost:" + port);
    }
}
