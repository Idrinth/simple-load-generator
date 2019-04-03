package de.idrinth.load;

import de.idrinth.load.concurrency.Request;
import de.idrinth.load.concurrency.ThreadPool;
import de.idrinth.load.validator.AssertionClassNotAvaible;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

class TestCase
{
    public RunnableFuture<Result> run(List<User> users, int perUser, String url, String name, long duration, String method, String body, Map asserts)
    {
        try {
            int parallel = users.size() * perUser;
            var result = new ResultCollector(url, name, method, parallel, new de.idrinth.load.validator.Factory().create(asserts));
            var executor = new ThreadPool(parallel);
            users.forEach((user) -> {
                String userUrl = url;
                for (String search : user.getReplacements().keySet()) {
                    userUrl = userUrl.replaceAll(search, user.getReplacements().get(search));
                }
                System.out.println("    " + userUrl + " x" + perUser);
                for (int i=0; i < perUser; i++) {
                    executor.add(new Request(user.getHeaders(), userUrl, result, method, body));
                }
            });
            executor.process(duration);
            return new FutureTask<>(result);
        } catch (InterruptedException|AssertionClassNotAvaible|ClassNotFoundException ex) {
            var result = new ResultCollector(url, name, method, 0, new ArrayList<>());
            result.add(ex);
            return new FutureTask<>(result);
        }
    }
}
