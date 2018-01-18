package org.apache.ignite.benchmark;

import javafx.util.Pair;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.model.MyPerson;
import org.apache.ignite.utils.CsvFileWriter;
import org.apache.ignite.utils.MyIgnite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WriteBenchmark {

    public static long PERSONS_CNT = 80000;
    public static IgniteCache<Long, MyPerson> cache;

    public static void clearCache(){
        System.out.println("Clearing the cache...");
        cache.clear();
    }

    public static void write() {

        // start client
        Ignite ignite = MyIgnite.start("client");
        cache = ignite.getOrCreateCache("MyPerson");

        Affinity aff = ignite.affinity("MyPerson");

        for (long personId = 0; personId < PERSONS_CNT; personId++) {
            // Get partition ID for the key under which myPerson is stored in cache.
            long partId = aff.partition(personId);

            MyPerson myPerson = new MyPerson(personId);
            myPerson.setSalary(personId*10);

            try {
                InputStream initialStream = new FileInputStream(new File("/Users/yuval/Workspace/GGvsGSbenchmark/src/main/resources/resume.txt"));
                //InputStream initialStream = new FileInputStream(new File("/home/xap/yuvald/resources/resume.txt"));
                byte[] buffer = new byte[initialStream.available()];
                initialStream.read(buffer);
                myPerson.setResume(initialStream.toString());
                initialStream.close();
            }catch(IOException e){
                System.out.println(e);
            }

            //myPerson.setPartitionId(partId);
            // Fill other fields.

            cache.put(personId, myPerson);
        }
    }

    public static void main(String[] args){
        WriteBenchmark.write();
        WriteBenchmark.clearCache();

        Pair<String, String> operationPair = new Pair<String, String>("Operation", "Write");
        Pair<String, Long> ObjNumberPair = new Pair<String, Long>("Obj Number", PERSONS_CNT);

        Long start = System.currentTimeMillis();

        // do write
        WriteBenchmark.write();

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


        System.exit(0);
    }
}
