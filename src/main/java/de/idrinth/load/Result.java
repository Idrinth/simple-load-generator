package de.idrinth.load;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class Result {
    private final String url;
    private final String name;
    private final String method;
    private final int parallel;
    private final BigDecimal requests;
    private final BigDecimal errors;
    private final BigDecimal duration;
    private final BigDecimal fastest;
    private final BigDecimal slowest;
    private final Map<String, Integer> messages;

    public Result(
        String name,
        String url,
        String method,
        int parallel,
        BigDecimal requests,
        BigDecimal errors,
        BigDecimal duration,
        BigDecimal fastest,
        BigDecimal slowest,
        Map<String, Integer> messages
    ) {
        this.name = name;
        this.url = url;
        this.method = method;
        this.parallel = parallel;
        this.requests = requests;
        this.errors = errors;
        this.duration = duration;
        this.fastest = fastest;
        this.slowest = slowest;
        this.messages = messages;
    }

    public String getMethod() {
        return method;
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
        try {
            return duration.divide(BigDecimal.valueOf(parallel), duration.scale(), RoundingMode.HALF_DOWN);
        } catch (ArithmeticException byZero) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getAverage() {
        try {
            return duration.divide(requests, duration.scale(), RoundingMode.HALF_UP);
        } catch (ArithmeticException byZero) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getRequestsPerSecond() {
        try {
            return requests.multiply(BigDecimal.valueOf(1000000000)).divide(duration, requests.scale(), RoundingMode.DOWN);
        } catch (ArithmeticException byZero) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getFastest() {
        return fastest;
    }

    public BigDecimal getSlowest() {
        return slowest;
    }

    public Map<String, Integer> getMessages() {
        return messages;
    }
}
