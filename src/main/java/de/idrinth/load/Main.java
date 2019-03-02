package de.idrinth.load;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main
{
    public static void main(String[] args)
    {
        try {
            var files = args.length == 0 ? "tests.yml" : args[0];
            var it = new Runner(files).run();
            System.out.println();
            System.out.println("URL Errors Requests Duration Fastest Slowest");
            for(var result : it) {
                var res = result.get();
                System.out.printf(
                    "%s %d %d %f %f %f\n",
                    res.getUrl(),
                    res.getErrors(),
                    res.getRequests(),
                    res.getDuration(),
                    res.getFastest(),
                    res.getSlowest()
                );
            }
        } catch (IOException|ExecutionException|InterruptedException ex) {
            System.err.println(ex);
        }
        System.out.println();
        System.exit(0);
    }
}
