package de.idrinth.load;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RunnableFuture;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

public class Runner
{
    private final List<User> users = new ArrayList<>();
    private final String baseUrl;
    private final Map<String, Map> tests;
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
                    (Map<String, String>) user.getOrDefault("replacements", new HashMap()))
                );
            });
        }
        if (users.isEmpty()) {
            users.add(new User(new HashMap(), new HashMap()));
        }
        System.out.println("End: Getting Users");
    }
    public List<RunnableFuture<Result>> run()
    {
        System.out.println("Start: Running tests");
        List<RunnableFuture<Result>> results = new ProcessingList();
        for(String name : tests.keySet()) {
            System.out.println("  Start: testcase "+name);
            Map test = (Map) tests.get(name);
            results.add(new TestCase(
                    users,
                    baseUrl + test.getOrDefault("url", ""),
                    name,
                    (int) test.getOrDefault("duration", 0),
                    (int) test.getOrDefault("threads", 0)
            ).test());
            System.out.println("  End: testcase "+name);
        }
        System.out.println("End: Running tests");
        return results;
    }
}
