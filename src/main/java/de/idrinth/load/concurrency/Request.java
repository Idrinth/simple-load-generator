package de.idrinth.load.concurrency;

import de.idrinth.load.ResultCollector;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Request implements Runnable {
    private final ResultCollector result;
    private final HttpGet request;
    private final CloseableHttpClient client;

    public Request(Map<String, String> headers, String url, ResultCollector result) {
        this.result = result;
        client = HttpClients.createDefault();
        request = new HttpGet(url);
        headers.keySet().forEach((header) -> {
            request.setHeader(header, headers.get(header));
        });
    }

    @Override
    public void run() {
        var start = LocalTime.now();
        try (CloseableHttpResponse response = client.execute(request)) {
            result.add(response.getStatusLine().getStatusCode(), Duration.between(start, LocalTime.now()));
        } catch (IOException ex) {
            System.err.println(ex);
            result.add(-1, Duration.between(start, LocalTime.now()));
        }
    }
}