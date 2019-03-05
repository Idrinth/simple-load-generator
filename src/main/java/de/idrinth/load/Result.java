package de.idrinth.load;

public class Result {
    private final String url;
    private final String name;
    private final int parallel;
    private final long requests;
    private final long errors;
    private final long duration;
    private final long fastest;
    private final long slowest;

    public Result(String name, String url, int parallel, long requests, long errors, long duration, long fastest, long slowest) {
        this.name = name;
        this.url = url;
        this.parallel = parallel;
        this.requests = requests;
        this.errors = errors;
        this.duration = duration;
        this.fastest = fastest;
        this.slowest = slowest;
    }

    public String getName() {
        return name;
    }

    public int getParallel() {
        return parallel;
    }

    public long getErrors() {
        return errors;
    }

    public String getUrl() {
        return url;
    }

    public long getRequests() {
        return requests;
    }

    public long getDuration() {
        return duration;
    }

    public long getAverage() {
        return duration/requests;
    }

    public long getRequestsPerSecond() {
        return requests/(duration/1000000000);
    }

    public long getFastest() {
        return fastest;
    }

    public long getSlowest() {
        return slowest;
    }
}
