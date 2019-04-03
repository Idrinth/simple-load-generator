package de.idrinth.load.validator;

import java.util.HashMap;
import java.util.Map;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;

class JsonValidator implements ResponseValidator
{
    private Schema schema;
    public JsonValidator(String schemaUrl) {
        try {
            var map = new HashMap<String, String>();
            map.put("$schema", schemaUrl);
            schema = SchemaLoader.builder()
                .schemaJson(new JSONObject(map))
                .build()
                .load()
                .build();
        } catch (Exception ex) {
            schema = null;
        }
    }
    public JsonValidator() {
        schema = null;
    }
    
    @Override
    public void validate(String body, Map<String, String> headers) throws AssertionFailed {
        try {
            var json = new JSONObject(body);
            if (schema != null) {
                schema.validate(json);
            }
        } catch(JSONException|ValidationException ex) {
            throw new AssertionFailed(this.getClass().getName(), ex);
        }
    }

    @Override
    public void validate(Map<String, String> headers) throws AssertionFailed {
        throw new AssertionFailed(this.getClass().getName(), "empty body can't be json.");
    }
    
}
