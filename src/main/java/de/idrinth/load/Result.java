package de.idrinth.load;

public class Result {
    private final String url;
    private final long requests;
    private final long errors;
    private final double duration;
    private final double fastest;
    private final double slowest;

    public Result(String url, long requests, long errors, double duration, double fastest, double slowest) {
        this.url = url;
        this.requests = requests;
        this.errors = errors;
        this.duration = duration;
        this.fastest = fastest;
        this.slowest = slowest;
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

    public double getDuration() {
        return duration;
    }

    public double getFastest() {
        return fastest;
    }

    public double getSlowest() {
        return slowest;
    }
    
}
