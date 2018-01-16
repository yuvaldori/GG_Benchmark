package org.apache.ignite.utils;

import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CsvFileWriter {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    public static void writeCsvFile(String fileName, List<Pair> resultsList) {

        FileWriter fileWriter = null;

        try {
            //String filePath = "/Users/yuval/Workspace/GGvsGSbenchmark/results/";
            String filePath = "/home/xap/yuvald/results/";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            fileWriter = new FileWriter(filePath+fileName);
            fileWriter.append("Date: " + dateFormat.format(date));

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            for (int i=0; i < resultsList.size(); i++){
                //Write the CSV file headers
                fileWriter.append((String)resultsList.get(i).getKey());

                fileWriter.append(COMMA_DELIMITER);
            }

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            for (int i=0; i < resultsList.size(); i++){
                //Write the CSV file headers
                fileWriter.append(String.valueOf(resultsList.get(i).getValue()));

                fileWriter.append(COMMA_DELIMITER);
            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }
}
