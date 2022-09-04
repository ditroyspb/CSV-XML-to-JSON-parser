import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCsv(columnMapping, fileName);
        System.out.println(list);
        String json = listToJson(list);
        System.out.println(json);
        writeString(json, "new_Data.json");

        List<Employee> listFromXML = parseXML("data.xml");
        String jsonFromXML = listToJson(listFromXML);
        writeString(jsonFromXML, "data2.json");
    }

    public static List<Employee> parseCsv(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileName) { //
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException {
        List<Employee> listFromXML_ = new ArrayList<>();
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                NodeList employeeChild = nodeList.item(i).getChildNodes();
                long id = 1;
                String firstName = "ex";
                String lastName = "ex";
                String country = "ex";
                int age = 1;
                for (int j = 0; j < employeeChild.getLength(); j++) {

                    switch (employeeChild.item(j).getNodeName()) {
                        case "id": {
                            id = Long.parseLong(employeeChild.item(j).getTextContent());
                            break;
                        }
                        case "firstName": {
                            firstName = employeeChild.item(j).getTextContent();
                            break;
                        }
                        case "lastName": {
                            lastName = employeeChild.item(j).getTextContent();
                            break;
                        }
                        case "country": {
                            country = employeeChild.item(j).getTextContent();
                            break;
                        }
                        case "age": {
                            age = Integer.parseInt(employeeChild.item(j).getTextContent());
                            break;
                        }
                    }

                }
                listFromXML_.add(new Employee(id, firstName, lastName, country, age));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return listFromXML_;
    }
}