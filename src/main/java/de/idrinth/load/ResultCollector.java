package de.idrinth.load;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

class ResultCollector implements Callable
{
    private final String url;
    private final Queue<Set> list = new ConcurrentLinkedQueue<>();

    public ResultCollector(String url) {
        this.url = url;
    }
    
    public void add(int status, Duration duration)
    {
        list.add(new Set(status, duration));
    }
    @Override
    public Result call()
    {
        double min = 0;
        double sum = 0;
        long count = 0;
        long errors = 0;
        double max = 0;
        for (Set set : list) {
            count++;
            double duration = set.duration.getSeconds() + set.duration.getNano()/1000000000;
            sum += duration;
            min = min == 0 || duration < min ? duration : min;
            max = duration > max ? duration : max;
            if (set.status < 200 || set.status > 299) {
                errors++;
                //System.err.print("E");
            } else {
                //System.out.print(".");
            }
        }
        return new Result(url, count, errors, sum, min, max);
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
