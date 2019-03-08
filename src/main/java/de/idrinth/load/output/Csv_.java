package de.idrinth.load.output;

import com.opencsv.CSVWriter;
import de.idrinth.load.Result;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Csv extends BaseOutput
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
                "URL",
                "Errors",
                "Requests",
                "Average(ns)",
                "Fastest(ns)",
                "Slowest(ns)"
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
            result.getUrl(),
            String.valueOf(result.getErrors()),
            String.valueOf(result.getRequests()),
            String.valueOf(result.getAverage()),
            String.valueOf(result.getFastest()),
            String.valueOf(result.getSlowest())
        });
    }
    @Override
    public void write() throws IOException
    {
        csv.close();
    }
}
