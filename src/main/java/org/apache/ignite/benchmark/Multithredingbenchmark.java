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

        Map<Long, MyPerson> personMap1 = DataBuilder.buildData(0L,100000L);
        WriteThread writeThread1 = new WriteThread(personMap1);
        Map<Long, MyPerson> personMap2 = DataBuilder.buildData(200000L,300000L);
        WriteThread writeThread2 = new WriteThread(personMap2);
        Map<Long, MyPerson> personMap3 = DataBuilder.buildData(400000L,600000L);
        WriteThread writeThread3 = new WriteThread(personMap3);
        Map<Long, MyPerson> personMap4 = DataBuilder.buildData(700000L,800000L);
        WriteThread writeThread4 = new WriteThread(personMap4);

        Set<Long> keys1 = DataBuilder.buildKeys(0L,100000L); //exist
        ReadThread readThread1 = new ReadThread(keys1);
        Set<Long> keys2 = DataBuilder.buildKeys(100000L,200000L); //not exist
        ReadThread readThread2 = new ReadThread(keys2);
        Set<Long> keys3= DataBuilder.buildKeys(250000L,350000L); // half exist and half not
        ReadThread readThread3 = new ReadThread(keys3);
        Set<Long> keys4 = DataBuilder.buildKeys(400000L,500000L);
        ReadThread readThread4 = new ReadThread(keys4);


        Pair<String, String> operationPair = new Pair<String, String>("Operation", "Multithreding Write Read");
        Pair<String, Long> ObjNumberPair = new Pair<String, Long>("Obj Number", 500000L);
        Long start = System.currentTimeMillis();

        writeThread1.start();
        readThread1.start();
        writeThread2.start();
        readThread2.start();
        writeThread3.start();
        readThread3.start();
        writeThread4.start();
        readThread4.start();

        // wait until all threads are finished
        try {
            writeThread1.join();
            readThread1.join();
            writeThread2.join();
            readThread2.join();
            writeThread3.join();
            readThread3.join();
            writeThread4.join();
            readThread4.join();

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
