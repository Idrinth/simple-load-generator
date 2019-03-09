package de.idrinth.load.output;

import de.idrinth.load.Result;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

public class Xml extends BaseOutput
{
    private final org.w3c.dom.Document document;

    public Xml() throws ParserConfigurationException
    {
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlVersion("1.0");
        document.appendChild(document.createElement("results"));
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
        test.appendChild(build("url", result.getUrl()));
        test.appendChild(build("parallel", result.getParallel()));
        test.appendChild(build("requests", result.getRequests()));
        test.appendChild(build("errors", result.getErrors()));
        test.appendChild(build("average", result.getAverage()));
        test.appendChild(build("fastest", result.getFastest()));
        test.appendChild(build("slowest", result.getSlowest()));
        test.appendChild(build("requests_per_second", result.getRequestsPerSecond()));
        document.getLastChild().appendChild(test);
    }
    private Element build(String name, String value)
    {
        var element = document.createElement(name);
        element.appendChild(document.createTextNode(value));
        return element;
    }
    private Element build(String name, long value)
    {
        return build(name, String.valueOf(value));
    }
    private Element build(String name, int value)
    {
        return build(name, String.valueOf(value));
    }
    private Element build(String name, double value)
    {
        return build(name, String.valueOf(value));
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
}
