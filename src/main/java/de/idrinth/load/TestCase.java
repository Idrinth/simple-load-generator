package de.idrinth.load;

import java.util.List;
import java.util.concurrent.RunnableFuture;

public class TestCase {
    private static final long DEFAULT_DURATION = 60;
    private static final int DEFAULT_THREADS = 5;
    private final List<User> users;
    private final String url;
    private final String name;
    private final long duration;
    private final int threads;

    public TestCase(List<User> users, String url, String name, long duration, int threads) {
        this.url = url;
        this.users = users;
        this.duration = duration > 0 ? duration : DEFAULT_DURATION;
        this.threads = threads > 0 ? threads : DEFAULT_THREADS;
        this.name = name;
    }

    public RunnableFuture<Result> test() {
        return new RequestHandler().run(users, threads, url, name, duration);
    }
}
