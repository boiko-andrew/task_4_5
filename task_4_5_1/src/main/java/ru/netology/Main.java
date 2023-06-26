package ru.netology;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.netology.employee.Employee;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> listStaff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            listStaff = csv.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return listStaff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String jsonFullFileName, String text) {
        try (FileWriter writer = new FileWriter(jsonFullFileName, false)) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static List<Employee> parseXml(String xmlFullFileName)
            throws ParserConfigurationException, IOException, SAXException {
        long id = 0L;
        String firstName = "";
        String lastName = "";
        String country = "";
        int age = 0;

        List<Employee> listStaff = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFullFileName));

        Node root = doc.getDocumentElement();
        NodeList firstLevelNodeList = root.getChildNodes();

        for (int i = 0; i < firstLevelNodeList.getLength(); i++) {
            Node firstLevelNode = firstLevelNodeList.item(i);

            if (firstLevelNode.getNodeType() == Node.ELEMENT_NODE) {
                Element firstLevelElement = (Element) firstLevelNode;
                NodeList secondLevelNodeList = firstLevelElement.getChildNodes();
                for (int j = 0; j < secondLevelNodeList.getLength(); j++) {
                    Node secondLevelNode = secondLevelNodeList.item(j);
                    if (secondLevelNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element secondLevelElement = (Element) secondLevelNode;
                        String nodeName = secondLevelElement.getNodeName();
                        String nodeText = secondLevelElement.getTextContent();

                        switch (nodeName) {
                            case ("id"):
                                id = Long.parseLong(nodeText);
                                break;
                            case ("firstName"):
                                firstName = nodeText;
                                break;
                            case ("lastName"):
                                lastName = nodeText;
                                break;
                            case ("country"):
                                country = nodeText;
                                break;
                            case ("age"):
                                age = Integer.parseInt(nodeText);
                        }
                    }
                }
                Employee employee = new Employee(id, firstName, lastName, country, age);
                listStaff.add(employee);
            }
        }
        return listStaff;
    }

    public static String readString(String jsonFullFileName) throws IOException {
        String outputString;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFullFileName))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
                if (line != null) {
                    stringBuilder.append(System.lineSeparator());
                }
            }
            outputString = stringBuilder.toString();
        }
        return outputString;
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> listStaff = new ArrayList<>();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            Employee employee = gson.fromJson(jsonObject, Employee.class);
            listStaff.add(employee);
        }
        return listStaff;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFullFileName = "./src/main/resources/data.csv";
        String jsonFullFileName = "./src/main/resources/data.txt";

        String xmlFullFileName = "./src/main/resources/data.xml";
        String json2FullFileName = "./src/main/resources/data2.txt";

        // task 4.5.1: CSV to JSON parser
        List<Employee> csvListStaff = parseCSV(columnMapping, csvFullFileName);
        String csvToJsonStaff = listToJson(csvListStaff);
        writeString(jsonFullFileName, csvToJsonStaff);

        // task 4.5.2: XML to JSON parser
        List<Employee> xmlListStaff = parseXml(xmlFullFileName);
        String xmlToJsonStaff = listToJson(xmlListStaff);
        writeString(json2FullFileName, xmlToJsonStaff);

        // task 4.5.3: JSON parser
        String json = readString(json2FullFileName);
        List<Employee> jsonListStaff = jsonToList(json);
        for (Employee employee : jsonListStaff) {
            System.out.println(employee);
        }
    }
}