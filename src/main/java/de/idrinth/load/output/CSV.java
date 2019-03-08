package de.idrinth.load.output;

import de.idrinth.load.Result;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

public class CSV implements Output
{
    private final CSVPrinter csv;
    private final NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH);
    private String suite = "";

    public CSV() throws IOException
    {
        csv = new CSVPrinter(
            new FileWriter(new File("results.csv")),
            CSVFormat.DEFAULT.withHeader("Testsuite", getResultFields().toString())
        );
    }
    private String[] getResultFields()
    {
        List list = new ArrayList<String>();
        for (var method : Result.class.getDeclaredMethods()) {
            if (!method.getName().equals("getClass") && !method.getName().equals("getName") && method.getName().startsWith("get") && method.getParameterCount() == 0) {
                list.add(method.getName().substring(3));
            }
        }
        return (String[]) list.toArray();
    }
    private static String getSuite(File config) throws IOException
    {
        var name = config.getName();
        if (name.contains(".")) {
            name = name.substring(0, name.indexOf("."));
        }
        return name;
    }
    @Override
    public void process(File file) throws IOException
    {
        suite = getSuite(file);
    }
    @Override
    public void process(Result result)
    {
        csv.printRecords(result);
    }
    @Override
    public void write() throws TransformerException, IOException
    {
        StringWriter sw = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(document), new StreamResult(sw));
        FileUtils.writeStringToFile(new File("results.xml"), sw.toString(), "UTF-8");
    }
    private String formatForOutput(NumberFormat formatter, Object data)
    {
        if (data instanceof String) {
            return (String) data;
        }
        if (data instanceof Long) {
            return formatter.format((Long) data);
        }
        if (data instanceof Integer) {
            return formatter.format((Integer) data);
        }
        return data == null ? "" : data.toString();
    }
}
