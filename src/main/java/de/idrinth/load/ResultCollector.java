package de.idrinth.load;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

class ResultCollector implements Callable
{
    private final String url;
    private final String name;
    private final int parallel;
    private final Queue<Set> list = new ConcurrentLinkedQueue<>();

    public ResultCollector(String url, String name, int parallel) {
        this.url = url;
        this.name = name;
        this.parallel = parallel;
    }
    
    public void add(int status, Duration duration)
    {
        list.add(new Set(status, duration));
    }
    @Override
    public Result call()
    {
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        BigDecimal errors = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ZERO;
        for (Set set : list) {
            count = count.add(BigDecimal.ONE);
            BigDecimal duration = BigDecimal.valueOf(set.duration.getSeconds()).multiply(BigDecimal.valueOf(1000000000)).add(BigDecimal.valueOf(set.duration.getNano()));
            sum = sum.add(duration);
            min = min.equals(BigDecimal.ZERO) || min.compareTo(duration) == 1 ? duration : min;
            max = duration.compareTo(max) == 1 ? duration : max;
            if (set.status < 200 || set.status > 299) {
                errors = errors.add(BigDecimal.ONE);
            }
        }
        return new Result(name, url, parallel, count, errors, sum, min, max);
    }
    private class Set
    {
        private final int status;
        private final Duration duration;

        public Set(int status, Duration duration) {
            this.status = status;
            this.duration = duration;
        }
    }
}
