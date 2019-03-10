package de.idrinth.load;

import java.math.BigDecimal;

public class Result {
    private final String url;
    private final String name;
    private final int parallel;
    private final BigDecimal requests;
    private final BigDecimal errors;
    private final BigDecimal duration;
    private final BigDecimal fastest;
    private final BigDecimal slowest;

    public Result(
        String name,
        String url,
        int parallel,
        BigDecimal requests,
        BigDecimal errors,
        BigDecimal duration,
        BigDecimal fastest,
        BigDecimal slowest
    ) {
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

    public BigDecimal getErrors() {
        return errors;
    }

    public String getUrl() {
        return url;
    }

    public BigDecimal getRequests() {
        return requests;
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public BigDecimal getAverage() {
        return duration.divide(requests, duration.scale());
    }

    public BigDecimal getRequestsPerSecond() {
        return requests.divide(duration, requests.scale()).multiply(BigDecimal.valueOf(1000000000));
    }

    public BigDecimal getFastest() {
        return fastest;
    }

    public BigDecimal getSlowest() {
        return slowest;
    }
}
