package com.simeio.amPractices;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.attribute.Href;
import com.webfirmframework.wffweb.tag.html.attribute.Rel;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.links.A;
import com.webfirmframework.wffweb.tag.html.links.Link;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.tables.*;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class JsonParse {

    public static void main(String[] args) throws IOException {
        String propFileName = "config.properties";

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(propFileName);

        Properties p = readPropertiesFile("config.properties");
        p.load(stream);

        String jsonPattern = p.getProperty("jsonValuePattern");
        String colName = p.getProperty("column_name");
        String fileName = p.getProperty("inputFileName");

        Path path = Paths.get(fileName);

        try (Reader reader = Files.newBufferedReader(path,
                StandardCharsets.UTF_8)) {

            JsonParser parser = new JsonParser();
            JsonElement tree = parser.parse(reader);

            JsonObject badaJson = tree.getAsJsonObject();
            String[] cVal = jsonPattern.split(",");
            String[] colNameArray = colName.split(",");

            HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

            ArrayList<ArrayList<String>> arrayList = new ArrayList<>();

            for (int i = 0; i < cVal.length; i++) {
                hashMap.put(colNameArray[i], getArrayVal(cVal[i], badaJson.toString()));
                arrayList.add(getArrayVal(cVal[i], badaJson.toString()));

            }
            System.out.println(hashMap);
            System.out.println(arrayList);

            Html roothtml = new Html(null).give(html -> {
                new Head(html).give(head -> {
                    new Link(head,
                            new Rel("stylesheet"),
                            new Href("https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"),
                            new CustomAttribute("integrity", "sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"),
                            new CustomAttribute("crossorigin", "anonymous"));
                });
            });
            Body body = new Body(roothtml);
            new NoTag(body, "Hello World");
            Table mainTable = new Table(body,
                    new ClassAttribute("table table-striped center"));


            for (String columnName : colNameArray) {
                Th th = new Th(mainTable);
                new NoTag(th, columnName);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                Tr tr = new Tr(mainTable);
                new NoTag(tr);

                for (ArrayList<String> strings : arrayList) {
                    Td td = new Td(tr);
                    new NoTag(td, strings.get(i).replaceAll("^\"|\"", ""));

                }
            }
            System.out.println(roothtml.toHtmlString(true));
        }
    }

    private static ArrayList<String> getArrayVal(String jsonPattern, String json) {
        String[] b = jsonPattern.split("-");
        String temp = JsonParser.parseString(json).toString();
        String a = temp.split("-")[0];
        ArrayList<String> list = new ArrayList<>();
        if (b[0].contains("[]")) {
            b[0] = b[0].substring(0, b[0].length() - 2);
            JsonArray jsonArray = JsonParser.parseString(temp).getAsJsonObject().get(b[0]).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                list.add(getValue(jsonPattern, jsonArray.get(i).toString()));
            }
        }
        return list;
    }

    private static String getValue(String jsonPattern, String jsonString) {
        String[] b = jsonPattern.split("-");
        String temp = JsonParser.parseString(jsonString).toString();
        for (String oName : b) {
            if (!oName.contains("[]")) {
                if (JsonParser.parseString(temp).isJsonObject()) {
                    temp = JsonParser
                            .parseString(temp)
                            .getAsJsonObject()
                            .get(oName)
                            .toString();
                }
            }
        }
        return temp;
    }

    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            // create Properties class object
            prop = new Properties();
            // load properties file into it
            prop.load(fis);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            fis.close();
        }

        return prop;
    }
}
