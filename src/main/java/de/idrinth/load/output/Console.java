package de.idrinth.load.output;

import de.idrinth.load.Result;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.xml.transform.TransformerException;

public class Console extends BaseOutput
{
    private final NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH);

    @Override
    public void process(File file) throws IOException
    {
        System.out.println(getSuite(file));
        System.out.printf(
            "\n%20s | %50s | %8s | %8s | %12s | %12s | %12s\n",
            "Test",
            "URL",
            "Errors",
            "Requests",
            "Average(ns)",
            "Fastest(ns)",
            "Slowest(ns)"
        );
    }
    @Override
    public void process(Result result)
    {
        System.out.printf(
            "%20s | %50s | %8s | %8s | %12s | %12s | %12s\n",
            result.getName(),
            result.getUrl(),
            result.getErrors(),
            result.getRequests(),
            formatter.format(result.getAverage()),
            formatter.format(result.getFastest()),
            formatter.format(result.getSlowest())
        );
    }
    @Override
    public void write() throws TransformerException, IOException
    {
    }
}
