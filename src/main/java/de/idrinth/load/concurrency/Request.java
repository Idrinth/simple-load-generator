package de.idrinth.load.concurrency;

import de.idrinth.load.ResultCollector;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Request implements Runnable {
    private final ResultCollector result;
    private final HttpRequestBase request;
    private final CloseableHttpClient client;

    public Request(Map<String, String> headers, String url, ResultCollector result, String method, String body) {
        this.result = result;
        client = HttpClients.createDefault();
        request = getRequest(method, url);
        if (body.length() > 0 && HttpEntityEnclosingRequest.class.isInstance(request)) {
            try {
                ((HttpEntityEnclosingRequest) request).setEntity(
                    new StringEntity(FileUtils.readFileToString(new File(body), "utf-8"), "utf-8")
                );
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
        headers.keySet().forEach((header) -> {
            request.setHeader(header, headers.get(header));
        });
    }
    private HttpRequestBase getRequest(String method, String url)
    {
        switch (method.toLowerCase()) {
            case "post":
                return new HttpPost(url);
            case "put":
                return new HttpPut(url);
            case "delete":
                return new HttpDelete(url);
            case "patch":
                return new HttpPatch(url);
            case "get":
            default:
                return new HttpGet(url);
        }
    }

    @Override
    public void run() {
        var start = LocalTime.now();
        try (CloseableHttpResponse response = client.execute(request)) {
            result.add(
                Duration.between(start, LocalTime.now()),
                response.getAllHeaders(),
                response.getEntity(),
                response.getStatusLine().getStatusCode()
            );
        } catch (IOException ex) {
            System.err.println(ex);
            result.add(ex);
        }
    }
}