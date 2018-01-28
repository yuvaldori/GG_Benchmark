package org.apache.ignite.benchmark;

import javafx.util.Pair;
import org.apache.ignite.model.MyPerson;
import org.apache.ignite.utils.CsvFileWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Multithredingbenchmark {

    public static void main(String[] args){
        Map<Long, MyPerson> personMap = DataBuilder.buildData(0L,100L);
        WriteThread writeThread = new WriteThread(personMap);

        Set<Long> keys = DataBuilder.buildKeys(0L,100L);
        ReadThread readThread = new ReadThread(keys);

        Pair<String, String> operationPair = new Pair<String, String>("Operation", "Multithreding Write Read");
        Pair<String, Long> ObjNumberPair = new Pair<String, Long>("Obj Number", 100L);
        Long start = System.currentTimeMillis();

        writeThread.start();
        readThread.start();

        // wait until both threads finished
        try {
            writeThread.join();
            readThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }


        Long end = System.currentTimeMillis();
        Long elapsedTime = end - start;
        Pair<String, Long> elapsedTimePair = new Pair<String, Long>("Elapsed Time", elapsedTime);

        List<Pair> results = new ArrayList<Pair>();
        results.add(operationPair);
        results.add(ObjNumberPair);
        results.add(elapsedTimePair);

        String fileName = System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter = new CsvFileWriter();
        csvFileWriter.writeCsvFile(fileName, results);

    }
}
