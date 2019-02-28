package de.idrinth.load;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        try {
            var files = args.length == 0 ? "tests.yml" : args[0];
            System.out.println("URL Errors/Requests Duration Fastest Slowest");
            for(var result : new Runner(files).run()) {
                System.out.printf(
                    "%s %d/%d %f %f %f\n",
                    result.getUrl(),
                    result.getErrors(),
                    result.getRequests(),
                    result.getDuration(),
                    result.getFastest(),
                    result.getSlowest()
                );
            }
        } catch (IOException ex) {
            System.err.print(ex);
        }
        System.out.println();
        System.exit(0);
    }
}
