package de.idrinth.load;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

public class ProcessingList extends ArrayList<RunnableFuture<Result>> 
{
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    public boolean add(RunnableFuture<Result> result)
    {
        executor.submit(result);
        return super.add(result);
    }
}
