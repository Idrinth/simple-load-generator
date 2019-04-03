package de.idrinth.load;

import de.idrinth.load.concurrency.ProcessingList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

class Runner
{
    private final List<User> users = new ArrayList<>();
    private final String baseUrl;
    private final Map<String, Map> tests;
    private static final int DEFAULT_DURATION = 60;
    private static final int DEFAULT_THREADS = 5;
    public Runner(File file) throws IOException
    {
        var yml = new Yaml();
        Map<String, Object> loaded = yml.load(FileUtils.openInputStream(file));
        baseUrl = (String) loaded.getOrDefault("base", "");
        getUsers((List<Map<String, Map<String, String>>>) loaded.get("users"));
        tests = (Map<String, Map>) loaded.get("tests");
    }
    private void getUsers(List<Map<String, Map<String, String>>> userData)
    {
        System.out.println("Start: Getting Users");
        if (userData != null) {
            userData.forEach((user) -> {
                users.add(new User(
                    (Map<String, String>) user.getOrDefault("headers", new HashMap()),
                    (Map<String, String>) user.getOrDefault("replacements", new HashMap()),
                    (Map<String, String>) user.getOrDefault("cookies", new HashMap())
                ));
            });
        }
        if (users.isEmpty()) {
            users.add(new User());
        }
        System.out.println("End: Getting Users");
    }
    public List<RunnableFuture<Result>> run()
    {
        System.out.println("Start: Running tests");
        List<RunnableFuture<Result>> results = new ProcessingList();
        var handler = new TestCase();
        for(String name : tests.keySet()) {
            System.out.println("  Start: testcase "+name);
            Map test = (Map) tests.get(name);
            results.add(handler.run(
                users,
                selfBiggerZeroOrDefault((int) test.getOrDefault("threads", 0), DEFAULT_THREADS),
                baseUrl + test.getOrDefault("url", ""),
                name,
                selfBiggerZeroOrDefault((int) test.getOrDefault("duration", 0), DEFAULT_DURATION),
                (String) test.getOrDefault("method", "get"),
                (String) test.getOrDefault("body", ""),
                (Map) test.getOrDefault("assert", new HashMap())
            ));
            System.out.println("  End: testcase "+name);
        }
        System.out.println("End: Running tests");
        return results;
    }
    private int selfBiggerZeroOrDefault(int test, int def)
    {
        return test > 0 ? test : def;
    }
}
