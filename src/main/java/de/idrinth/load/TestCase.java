package de.idrinth.load;

import de.idrinth.load.concurrency.Request;
import de.idrinth.load.concurrency.ThreadPool;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

class TestCase
{
    public RunnableFuture<Result> run(List<User> users, int perUser, String url, String name, long duration)
    {
        int parallel = users.size() * perUser;
        var result = new ResultCollector(url, name, parallel);
        try {
            var executor = new ThreadPool(parallel);
            users.forEach((user) -> {
                String userUrl = url;
                for (String search : user.getReplacements().keySet()) {
                    userUrl = userUrl.replaceAll(search, user.getReplacements().get(search));
                }
                System.out.println("    " + userUrl + " x" + perUser);
                for (int i=0; i < perUser; i++) {
                    executor.add(new Request(user.getHeaders(), userUrl, result));
                }
            });
            executor.process(duration);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
        return new FutureTask<>(result);
    }
}
