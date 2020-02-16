package main.java;

import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TestData {

    public static String getUser(String key, String node) {
        int size;
        String result = "";
        File file = new File("testdata/user.json");

        try {
            size = JsonPath.read(file, "$.length()");
            for (int i=0; i<size; i++) {
                String path = "$.[" + i + "].email";
                if (node.equals(JsonPath.read(file, path))) {
                    path = "$.[" + i + "]." + key;
                    result = JsonPath.read(file, path);
                    break;
                }
            }
        } catch (IOException e) {}

        if (result.length()>0 && result.charAt(0) == '%') {
            result = result.substring(1);
            result = result.substring(0, result.indexOf('%'));
            result = System.getenv(result);
        }

        return result;
    }

    public static String getUser(String key, int index) {
        int size;
        String result = "";
        File file = new File("testdata/user.json");

        try {
            index --;   // convert index from natural to array form
            size = JsonPath.read(file, "$.length()");

            if (index < 0) index = new Random().nextInt(size-1);
            if (index >= size) index = size - 1;

            String path = "$.[" + index + "]." + key;
            result = JsonPath.read(file, path);
        } catch (IOException e) {}

        if (result.length()>0 && result.charAt(0) == '%') {
            result = result.substring(1);
            result = result.substring(0, result.indexOf('%'));
            result = System.getenv(result);
        }

        return result;
    }

    public static String getProperty(String key) {
        return getValue ("testdata/properties.json", key);
    }

    public static String getAssertions(String key) {
        return getValue ("testdata/assertions.json", key);
    }

    private static String getValue (String fName, String key) {
        String result = "";
        File file = new File(fName);

        try {
            String path = "$." + key;
            result = JsonPath.read(file, path);
        } catch (IOException e) {}

        if (result.length()>0 && result.charAt(0) == '%') {
            result = result.substring(1);
            result = result.substring(0, result.indexOf('%'));
            result = System.getenv(result);
        }
        return result;
    }

}
