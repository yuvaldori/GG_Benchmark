package org.apache.ignite.benchmark;

import javafx.util.Pair;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.utils.CsvFileWriter;
import org.apache.ignite.utils.MyIgnite;

import java.util.ArrayList;
import java.util.List;

public class BasicPutGetBenchmark {

    public static Long numberOfWrites = new Long(100000);
    public static Long numberOfReads = new Long(100000);;

    public void execute(){

        // Starting the node.
        Ignite ignite = MyIgnite.start("client");

        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCacheName");

        // Store keys in cache (values will end up on different cache nodes).
        for (int i = 0; i < numberOfWrites; i++)
            cache.put(i, Integer.toString(i));

        for (int i = 0; i < numberOfReads; i++)
            //System.out.println("Got [key=" + i + ", val=" + cache.get(i) + ']');
            cache.get(i);
    }

    public static void main(String[] args){
        BasicPutGetBenchmark dataGridBenchmark = new BasicPutGetBenchmark();
        Long start = System.currentTimeMillis();
        dataGridBenchmark.execute();
        Long end = System.currentTimeMillis();
        Long elapsedTime = end - start;

        Pair<String, String> benchmarkPair = new Pair<String, String>("Benchmark", "BasicPutGet");
        Pair<String, Long> writePair = new Pair<String, Long>("Writes", numberOfWrites);
        Pair<String, Long> readPair = new Pair<String, Long>("Reads", numberOfReads);
        Pair<String, Long> elapsedTimePair = new Pair<String, Long>("Elapsed time(ms)", elapsedTime);

        List<Pair> results = new ArrayList<Pair>();
        results.add(benchmarkPair);
        results.add(writePair);
        results.add(readPair);
        results.add(elapsedTimePair);

        String fileName = System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter = new CsvFileWriter();
        csvFileWriter.writeCsvFile(fileName, results);
        System.exit(0);
    }
}
