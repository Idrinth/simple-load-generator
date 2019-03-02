package de.idrinth.load;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class RequestHandler
{
    public RunnableFuture<Result> run(List<User> users, int perUser, String url, long duration)
    {
        var result = new ResultCollector(url);
        try {
            ExecutorService executor = Executors.newFixedThreadPool(users.size() * perUser);
            users.forEach((user) -> {
                String userUrl = url;
                for (String search : user.getReplacements().keySet()) {
                    userUrl = userUrl.replaceAll(search, user.getReplacements().get(search));
                }
                System.out.println("    " + userUrl + " x" + perUser);
                for (int i=0; i < perUser; i++) {
                    executor.submit(new RestartingRequest(user.getHeaders(), userUrl, result, LocalTime.now().plusSeconds((int) (duration*1.01))));
                }
            });
            executor.shutdown();
            executor.awaitTermination(duration*2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
        return new FutureTask<>(result);
    }
    private class RestartingRequest implements Runnable {
        private final ResultCollector result;
        private final HttpGet request;
        private final CloseableHttpClient client;
        private final LocalTime end;

        public RestartingRequest(Map<String, String> headers, String url, ResultCollector result, LocalTime end) {
            this.result = result;
            client = HttpClients.createDefault();
            request = new HttpGet(url);
            headers.keySet().forEach((header) -> {
                request.setHeader(header, headers.get(header));
            });
            this.end = end;
        }
        
        @Override
        public void run() {
            while(!LocalTime.now().minusSeconds(1).isAfter(end)) {
                var start = LocalTime.now();
                try (CloseableHttpResponse response = client.execute(request)) {
                    result.add(response.getStatusLine().getStatusCode(), Duration.between(start, LocalTime.now()));
                } catch (IOException ex) {
                    System.err.println(ex);
                    result.add(-1, Duration.between(start, LocalTime.now()));
                }
            }
        }
    }
}
