package com.simeio.amPractices;

import com.webfirmframework.wffweb.tag.html.*;
import com.webfirmframework.wffweb.tag.html.attribute.Href;
import com.webfirmframework.wffweb.tag.html.attribute.Rel;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.links.Link;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.StyleTag;
import com.webfirmframework.wffweb.tag.html.tables.Table;
import com.webfirmframework.wffweb.tag.html.tables.Td;
import com.webfirmframework.wffweb.tag.html.tables.Th;
import com.webfirmframework.wffweb.tag.html.tables.Tr;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileParser {

    public static void main(String[] args) throws IOException {

        String fileName = args[0];

        String actualData = readFile(fileName);

        if (actualData!=null){

            String columns = actualData.split("\n")[0];
            String[] column = columns.split(",");

            String htmltoString = generateHTMLFromList(column,parseTheList(actualData));
            BufferedWriter writer = new BufferedWriter(new FileWriter("Result.html"));
            writer.write(htmltoString);
            writer.close();

            System.out.println("HTML Result Generated");
        }else{

            System.out.println("show not present");

        }



    }

    private static ArrayList<ArrayList<String>> parseTheList(String actualData){
        String columns = actualData.split("\n")[0];

        String[] columnsData = actualData.split("\n");
        columns = columns.replaceAll("\\s+", ",");


        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        for (int i = 1; i < columnsData.length; i++) {
            String c = columnsData[i].replaceAll("\\s+", ",");
            String rowData[] = c.split(",");
            ArrayList<String> arrayList1 = new ArrayList<>();
            for (String data : rowData) {
                arrayList1.add(data);
            }
            arrayList.add(arrayList1);
        }

        return arrayList;

    }

    private static String readFile(String fileName){
        try{
            InputStream inputStream;
            inputStream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String st;

            StringBuffer stringBuffer = new StringBuffer();
            while ((st = br.readLine()) != null) {
                stringBuffer.append(st);
                stringBuffer.append("\n");
            }
            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            System.out.printf("FileNotFoundException");
        } catch (UnsupportedEncodingException e) {
            System.out.printf("UnsupportedEncodingException");
        } catch (IOException e) {
            System.out.printf("IOException");
        }
        return null;

    }

    private static String generateHTMLFromList(String[] column, ArrayList<ArrayList<String>> arrayList) {


        Html rootTag = new Html(null).give(html -> {
            new Head(html).give(head -> {
                new StyleTag(head).give(style -> {
                    new NoTag(style, "#customers {\n  font-family: Arial, Helvetica, sans-serif;\n  border-collapse: collapse;\n  width: 100%;\n}\n\n#customers td, #customers th {\n  border: 1px solid #ddd;\n  padding: 8px;\n}\n\n#customers tr:nth-child(even){background-color: #f2f2f2;}\n\n#customers tr:hover {background-color: #ddd;}\n\n#customers th {\n  padding-top: 12px;\n  padding-bottom: 12px;\n  text-align: left;\n  background-color: #04AA6D;\n  color: white;\n}");
                });
            });
        });

        Body body = new Body(rootTag);

        P p = new P(body);
        new NoTag(p, "Hi,");
        new Br(p);

        P p2 = new P(body);
        new NoTag(p2, "Below is the Nightly Build Test Report");


        Table mainTable = new Table(body, new Id("customers"));

        for (String columnName : column) {
            Th th = new Th(mainTable);
            new NoTag(th, columnName);
        }

        for (ArrayList<String> strings : arrayList) {

            Tr tr = new Tr(mainTable);
            new NoTag(tr);
            for (String string : strings) {
                Td td = new Td(tr);
                String rowData = string;
                if (rowData.contains("<") || rowData.contains(">")) {
                    rowData = rowData.replaceAll("<", "");
                    rowData = rowData.replaceAll(">", "");
                }

                new NoTag(td, rowData);

            }
        }

        return rootTag.toHtmlString(true);

    }

}
