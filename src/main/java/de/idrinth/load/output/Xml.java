package de.idrinth.load.output;

import de.idrinth.load.Result;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;

public class Xml implements Output
{
    private final org.w3c.dom.Document document;
    private final NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH);

    public Xml() throws ParserConfigurationException
    {
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlVersion("1.0");
        document.appendChild(document.createElement("results"));
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
        Element root = document.createElement("suite");
        root.setAttribute("name", getSuite(file));
        root.setAttribute("date", LocalDate.now(ZoneId.of("UTC")).toString());
        root.setAttribute("time", LocalTime.now(ZoneId.of("UTC")).toString());
        document.getDocumentElement().appendChild(root);
    }
    @Override
    public void process(Result result)
    {
        var test = document.createElement("test");
        test.setAttribute("name", result.getName());
        for(var method : result.getClass().getMethods()) {
            if (!method.getName().equals("getClass") && !method.getName().equals("getName") && method.getName().startsWith("get") && method.getParameterCount() == 0) {
                try {
                    var node = document.createElement(method.getName().substring(3).toLowerCase());
                    node.appendChild(document.createTextNode(formatForOutput(formatter, method.invoke(result))));
                    test.appendChild(node);
                } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
                    System.err.println(ex);
                }
            }
        }
        document.getLastChild().appendChild(test);
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
