package de.idrinth.load.concurrency;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool extends ThreadPoolExecutor
{
    private volatile LocalTime end;
    private final Queue<Runnable> list = new LinkedList<>();
    public ThreadPool(int PoolSize) {
        super(PoolSize, PoolSize, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }
    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
        if (isShutdown() || isTerminating() || isTerminated()) {
            return;
        }
        if (LocalTime.now().isAfter(end)) {
            shutdown();
            return;
        }
        execute(r);
    }
    public void add(Runnable run) {
        this.list.add(run);
    }
    public boolean process(long timeout) throws InterruptedException
    {
        end = LocalTime.now().plusSeconds(timeout).plusNanos(list.size());
        list.forEach((run) -> {
            execute(run);
        });
        return super.awaitTermination(timeout * 2, TimeUnit.SECONDS);
    }
}
