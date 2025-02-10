package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Converter {
@SuppressWarnings("unchecked")
public static String jsonToCsv(String jsonString) {
    try {
        JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());

        JSONArray prodNums = (JSONArray) jsonObject.get("ProdNums");
        JSONArray colHeadings = (JSONArray) jsonObject.get("ColHeadings");
        JSONArray data = (JSONArray) jsonObject.get("Data");

        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");

        // Write column headers
        String[] headerRow = new String[colHeadings.size()];
        for (int i = 0; i < colHeadings.size(); i++) {
            headerRow[i] = (String) colHeadings.get(i);
        }
        csvWriter.writeNext(headerRow);

        // Write data rows
        for (int i = 0; i < data.size(); i++) {
            JSONArray rowArray = (JSONArray) data.get(i);
            List<String> rowList = new ArrayList<>();

            rowList.add((String) prodNums.get(i)); // First column is "ProdNum"

            for (int j = 0; j < rowArray.size(); j++) {
                if (j == 1 || j == 2) { // Convert Season & Episode back to zero-padded format
                    rowList.add(String.format("%02d", ((Number) rowArray.get(j)).intValue()));
                } else {
                    rowList.add(rowArray.get(j).toString());
                }
            }

            csvWriter.writeNext(rowList.toArray(new String[0]));
        }

        csvWriter.close();

        // Debugging Output
        System.out.println("========== JSON Input ==========");
        System.out.println(jsonString);
        System.out.println("========== Generated CSV ==========");
        System.out.println(writer.toString());

        return writer.toString().trim();

    } catch (Exception e) {
        e.printStackTrace();
        return "";


    
    
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        try {
            // Read CSV data
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> csvData = reader.readAll();
            reader.close();

            if (csvData.isEmpty()) return "{}";  // Ensure non-empty input

            JSONArray prodNums = new JSONArray();
            JSONArray colHeadings = new JSONArray();
            JSONArray dataArray = new JSONArray();

            // Extract column headers
            String[] headers = csvData.get(0);
            for (String header : headers) {
                colHeadings.add(header);
            }

            // Process data rows
            for (int i = 1; i < csvData.size(); i++) {
                String[] row = csvData.get(i);
                prodNums.add(row[0]); // Store first column in ProdNums
                
                JSONArray rowArray = new JSONArray();
                for (int j = 1; j < row.length; j++) {
                    if (j == 2 || j == 3) { // Convert Season & Episode to Integer if numeric
                        if (row[j].matches("\\d+")) {  // Check if it's a pure number
                            rowArray.add(Integer.parseInt(row[j])); 
                        } else {
                            rowArray.add(row[j]); // Keep as string if not numeric
                        }
                    } else {
                        rowArray.add(row[j]); // Store other fields as Strings
                    }
                }
                dataArray.add(rowArray);
            }

            // Construct JSON object
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("ProdNums", prodNums);
            jsonObject.put("ColHeadings", colHeadings);
            jsonObject.put("Data", dataArray);

            return Jsoner.serialize(jsonObject).trim(); 

        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; // Return empty JSON in case of failure
        }
    }
}
