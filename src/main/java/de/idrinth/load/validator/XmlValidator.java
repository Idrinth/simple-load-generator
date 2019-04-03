package de.idrinth.load.validator;

import java.io.IOException;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

public class XmlValidator implements ResponseValidator
{
    private DocumentBuilder builder;
    public XmlValidator(String schemaUrl) throws ParserConfigurationException {
        this();
    }
    public XmlValidator() throws ParserConfigurationException {
        this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    @Override
    public void validate(String body, Map<String, String> headers) throws AssertionFailed {
        try {
            builder.parse(IOUtils.toInputStream(body));
        } catch (SAXException|IOException ex) {
            throw new AssertionFailed(this.getClass().getName(), ex);
        }
    }

    @Override
    public void validate(Map<String, String> headers) throws AssertionFailed {
        throw new AssertionFailed(this.getClass().getName(), "empty body can't be xml.");
    }
}
