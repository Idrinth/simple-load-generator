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
        long min = 0;
        long sum = 0;
        long count = 0;
        long errors = 0;
        long max = 0;
        for (Set set : list) {
            count++;
            long duration = set.duration.getSeconds()*1000000000 + set.duration.getNano();
            sum += duration;
            min = min == 0 || duration < min ? duration : min;
            max = duration > max ? duration : max;
            if (set.status < 200 || set.status > 299) {
                errors++;
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
