package de.idrinth.load;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class RequestHandler
{
    public Result run(List<User> users, int perUser, String url, long duration)
    {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(users.size() * perUser);
            var result = new ResultCollector(url);
            users.forEach((user) -> {
                String userUrl = url;
                for (String search : user.getReplacements().keySet()) {
                    userUrl = userUrl.replaceAll(search, user.getReplacements().get(search));
                }
                for (int i=0; i < perUser; i++) {
                    executor.submit(new RestartingRequest(user.getHeaders(), userUrl, result, LocalTime.now().plusSeconds((int) (duration*1.01))));
                }
            });
            executor.shutdown();
            executor.awaitTermination(duration, TimeUnit.SECONDS);
            return result.calculate();
        } catch (InterruptedException ex) {
           // Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Result(url, 0, 1, 0, 0, 0);
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
                    //Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                    result.add(-1, Duration.between(start, LocalTime.now()));
                }
            }
        }
    }
}
