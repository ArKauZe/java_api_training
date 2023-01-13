package fr.lernejo.navy_battle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LauncherTest {
    @Test
    void pingTest() throws Exception {
        Launcher.main(new String[]{"8080"});
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/ping"))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(response.body()).isEqualTo("OK");
    }

    @Test
    void noArgsTest() {
        Assertions.assertThatThrownBy(() -> Launcher.main(new String[]{}))
            .isInstanceOf(ArrayIndexOutOfBoundsException.class)
            .hasMessage("Index 0 out of bounds for length 0");
    }

    @Test
    void OneArgTest() throws IOException, InterruptedException {
        Launcher.main(new String[]{"8888"});
        assertTrue(true);
    }

    @Test
    void TwoArgsTest() throws IOException, InterruptedException {
        Launcher.main(new String[]{"9999", "http://localhost:9999"});
        assertTrue(true);
    }

    @Test
    void wrongArgsTest() {
        Assertions.assertThatThrownBy(() -> Launcher.main(new String[]{"abc"}))
            .isInstanceOf(NumberFormatException.class)
            .hasMessage("Port must be a number");
    }

    @Test
    void tooManyArgsTest() {
        Assertions.assertThatThrownBy(() -> Launcher.main(new String[]{"8080", "http://localhost:8080", "abc"}))
            .isInstanceOf(NumberFormatException.class)
            .hasMessage("Port must be a number");
    }
}
