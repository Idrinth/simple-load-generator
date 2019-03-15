package de.idrinth.load.output;

import com.opencsv.CSVWriter;
import de.idrinth.load.Result;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Csv extends BaseOutput
{
    private final CSVWriter csv;
    private String suite = "";

    public Csv() throws IOException
    {
        csv = new CSVWriter(new FileWriter(new File("results.csv")));
        csv.writeNext(
            new String[] {
                "TestSuite",
                "Name", 
                "Method",
                "URL",
                "Errors",
                "Requests",
                "Average(ns)",
                "Fastest(ns)",
                "Slowest(ns)",
                "Duration(ns)",
                "R/s"
            },
            true
        );
    }
    @Override
    public void process(File file) throws IOException
    {
        suite = getSuite(file);
    }
    @Override
    public void process(Result result)
    {
        csv.writeNext(new String[] {
            suite,
            result.getName(),
            result.getMethod(),
            result.getUrl(),
            String.valueOf(result.getErrors()),
            String.valueOf(result.getRequests()),
            String.valueOf(result.getAverage()),
            String.valueOf(result.getFastest()),
            String.valueOf(result.getSlowest()),
            String.valueOf(result.getDuration()),
            String.valueOf(result.getRequestsPerSecond())
        });
    }
    @Override
    public void write() throws IOException
    {
        csv.close();
    }
}
