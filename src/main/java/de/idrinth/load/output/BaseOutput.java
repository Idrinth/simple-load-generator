package de.idrinth.load.output;

import java.io.File;
import java.io.IOException;

abstract class BaseOutput implements Output
{
    protected String getSuite(File config) throws IOException
    {
        var name = config.getName();
        if (name.contains(".")) {
            name = name.substring(0, name.indexOf("."));
        }
        return name;
    }
}
