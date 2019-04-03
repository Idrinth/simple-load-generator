package de.idrinth.load;

import de.idrinth.load.validator.ResponseValidator;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class ResultCollector implements Callable
{
    private final String url;
    private final String name;
    private final String method;
    private final int parallel;
    private final Queue<Set> list = new ConcurrentLinkedQueue<>();
    private final List<ResponseValidator> asserts;

    public ResultCollector(String url, String name, String method, int parallel, List<ResponseValidator> asserts) {
        this.url = url;
        this.name = name;
        this.parallel = parallel;
        this.method = method;
        this.asserts = asserts;
    }
    
    public void add(Duration duration, Header[] headers, HttpEntity body, int status)
    {
        list.add(new CheckableSet(duration, headers, body, status, asserts));
    }
    public void add(Exception exception)
    {
        list.add(new ExceptionSet(exception));
    }
    @Override
    public Result call()
    {
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        BigDecimal errors = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ZERO;
        Map<String, Integer> messages = new HashMap<>();
        for (Set set : list) {
            try {
                set.validate();
                set = (CheckableSet) set;
                count = count.add(BigDecimal.ONE);
                BigDecimal duration = BigDecimal
                    .valueOf(set.duration().getSeconds())
                    .multiply(BigDecimal.valueOf(1000000000))
                    .add(BigDecimal.valueOf(set.duration().getNano()));
                sum = sum.add(duration);
                min = min.equals(BigDecimal.ZERO) || min.compareTo(duration) == 1 ? duration : min;
                max = duration.compareTo(max) == 1 ? duration : max;
            } catch (Exception e) {
                errors = errors.add(BigDecimal.ONE);
                messages.put(e.toString(), messages.containsKey(e.toString())?messages.get(e.toString())+1:1);
            }
        }
        return new Result(name, url, method, parallel, count, errors, sum, min, max, messages);
    }
    private interface Set {
        public void validate() throws Exception;
        public Duration duration();
    }
    private class CheckableSet implements Set
    {
        private final Map<String, String> headers = new HashMap<>();
        private final Duration duration;
        private String body = null;
        private final List<ResponseValidator> validators;

        public CheckableSet(Duration duration, Header[] headers, HttpEntity body, int status, List<ResponseValidator> validators) {
            this.headers.put("@STATUS", String.valueOf(status));
            for (var header : headers) {
                this.headers.put(header.getName().toLowerCase(), header.getValue());
            }
            this.duration = duration;
            if (null != body) {
                try {
                    this.body = body.getContentEncoding() != null
                        ? IOUtils.toString(body.getContent(), body.getContentEncoding().getValue())
                        : IOUtils.toString(body.getContent(), Charset.defaultCharset());
                } catch (IOException|UnsupportedOperationException ex) {
                    Logger.getLogger(ResultCollector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.validators = validators;
        }
        @Override
        public void validate() throws Exception
        {
            for (var validator : validators) {
                if (body == null) {
                    validator.validate(headers);
                } else {
                    validator.validate(body, headers);
                }
            }
        }

        @Override
        public Duration duration() {
            return duration;
        }
    }
    private class ExceptionSet implements Set
    {
        private final Exception exception;

        public ExceptionSet(Exception exception) {
            this.exception = exception;
        }
        @Override
        public void validate() throws Exception
        {
            throw this.exception;
        }

        @Override
        public Duration duration() {
            return null;
        }
    }
}
