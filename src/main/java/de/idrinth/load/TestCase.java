package de.idrinth.load;

import java.util.List;

public class TestCase {
    private static final long DEFAULT_DURATION = 60;
    private static final int DEFAULT_THREADS = 5;
    private final List<User> users;
    private final String url;
    private final long duration;
    private final int threads;

    public TestCase(List<User> users, String url, long duration, int threads) {
        this.url = url;
        this.users = users;
        this.duration = duration > 0 ? duration : DEFAULT_DURATION;
        this.threads = threads > 0 ? threads : DEFAULT_THREADS;
    }
    

    public Result test() {
        return new RequestHandler().run(users, threads, url, duration);
    }
}
