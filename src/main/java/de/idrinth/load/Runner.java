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
    private List<User> users = new ArrayList<>();
    private final String baseUrl;
    private final Map<String, Object> tests;
    public Runner(String filename) throws IOException
    {
        var yml = new Yaml();
        Map<String, Object> loaded = yml.load(FileUtils.openInputStream(new File(filename)));
        baseUrl = (String) loaded.getOrDefault("base", "");
        getUsers((List<Map<String, Map<String, String>>>) loaded.get("users"));
        tests = (Map) loaded.get("tests");
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
        tests.keySet().stream().map((String name) -> (Map) tests.get(name)).forEachOrdered((test) -> {
            System.out.println("  Start: testcase");
            results.add(new TestCase(
                    users,
                    baseUrl + test.getOrDefault("url", ""),
                    (int) test.getOrDefault("duration", 0),
                    (int) test.getOrDefault("threads", 0)
            ).test());
            System.out.println("  End: testcase");
        });
        System.out.println("End: Running tests");
        return results;
    }
}
