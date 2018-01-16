package org.apache.ignite.benchmark;

import javafx.util.Pair;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.TextQuery;
import org.apache.ignite.model.MyPerson;
import org.apache.ignite.utils.CsvFileWriter;
import org.apache.ignite.utils.MyIgnite;
import org.apache.ignite.cache.query.QueryCursor;

import javax.cache.Cache;
import java.util.*;
/*
Ignite supports a very elegant query API with support for Predicate-based Scan Queries,
SQL Queries (ANSI-99 compliant), and Text Queries. For SQL queries ignites supports in-memory indexing,
so all the data lookups are extremely fast. If you are caching your data in off-heap memory,
then query indexes will also be cached in off-heap memory as well.
Ignite also provides support for custom indexing via IndexingSpi and SpiQuery class.
 */
public class QueriesBenchmark {

    /*
       Asynchronously gets a collection of entries from the {@link Cache}, returning them as
       {@link Map} of the values associated with the set of keys requested.
     */
    public static long getByKeys(Set<Long> keys){
        // Starting the node.
        Ignite ignite = MyIgnite.getInstanceAndStart();
                IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");
        Map<Long, MyPerson> personsMap = cache.getAll(keys);
        return personsMap.size();
    }

    /*
     * Similar to XAP Projection
     * Scan query with filter.
     * Scan queries also support optional transformer closure which allows to convert the entry on the server node before sending it to the client.
     * This is useful, for example, when you want to fetch only several fields out of large object and want to minimize the network traffic.
     * Example below shows how to fetch only keys and do not send value objects at all.
     * Get only keys for persons earning more than 1,000.
     */
    public static long scanQuery(){

        // Starting the node.
        Ignite ignite = MyIgnite.getInstanceAndStart();
        IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");

        List<Long> keys = cache.query(new ScanQuery<Long, MyPerson>(
                        (k, p) -> p.getSalary() > 1000), // Remote filter.
                Cache.Entry::getKey              // Transformer.
        ).getAll();

        return keys.size();
    }

    /*
    * Constructs query for the given search string.
    */
    public static long testQueries(){

        // Starting the node.
        Ignite ignite = MyIgnite.getInstanceAndStart();
        IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");

        // Query for all people with "Master Degree" in their resumes.
        TextQuery txt = new TextQuery(MyPerson.class, "Master Degree");

        QueryCursor<Map.Entry<Long, MyPerson>> masters = null;

        try {
            masters = cache.query(txt);

//            for (Map.Entry<Long, MyPerson> e : masters)
//                System.out.println(e.getValue().toString());
//            }
        }catch(Exception e){
            System.out.println(e);
        }
            if (masters != null)
                return masters.getAll().size();
            else
                return 0;
        }

    public static void main(String[] args){

        /*
        1) WRITE
         */
        Pair<String, String> operationPair1 = new Pair<String, String>("Operation", "Write");
        Pair<String, Long> ObjNumberPair1 = new Pair<String, Long>("Obj Number", WriteBenchmark.PERSONS_CNT);

        Long start = System.currentTimeMillis();

        WriteBenchmark.write();

        Long end = System.currentTimeMillis();
        Long elapsedTime = end - start;
        Pair<String, Long> elapsedTimePair1 = new Pair<String, Long>("Elapsed Time", elapsedTime);

        List<Pair> results = new ArrayList<Pair>();
        results.add(operationPair1);
        results.add(ObjNumberPair1);
        results.add(elapsedTimePair1);

        String fileName1 = System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter1 = new CsvFileWriter();
        csvFileWriter1.writeCsvFile(fileName1, results);


        /*
        2) COUNT KEYS
         */
        Set<Long> keys = new HashSet<Long>();
        for (long i=0; i<WriteBenchmark.PERSONS_CNT; i++){
            keys.add(i);
        }

        Long start2 = System.currentTimeMillis();
        long matched2 = QueriesBenchmark.getByKeys(keys);
        Long end2 = System.currentTimeMillis();
        Long elapsedTime2 = end2 - start2;

        Pair<String, String> benchmarkPair2 = new Pair<String, String>("Benchmark", "Queries/getByKeys()");
        Pair<String, Long> keysPair2 = new Pair<String, Long>("Number of keys", WriteBenchmark.PERSONS_CNT);
        Pair<String, Long> matchedPair2 = new Pair<String, Long>("Matched", matched2);
        Pair<String, Long> elapsedTimePair2 = new Pair<String, Long>("Elapsed time(ms)", elapsedTime2);

        List<Pair> results2 = new ArrayList<Pair>();
        results2.add(benchmarkPair2);
        results2.add(keysPair2);
        results2.add(matchedPair2);
        results2.add(elapsedTimePair2);

        String fileName2 = System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter2 = new CsvFileWriter();
        csvFileWriter2.writeCsvFile(fileName2, results2);


        /*
        3) COUNT WHERE SALARY > 1000
         */
        Long start3 = System.currentTimeMillis();
        long matched3 = QueriesBenchmark.scanQuery();
        Long end3 = System.currentTimeMillis();
        Long elapsedTime1 = end3 - start3;

        Pair<String, String> benchmarkPair3 = new Pair<String, String>("Benchmark", "Queries/scanQuery()");
        Pair<String, Long> keysPair3 = new Pair<String, Long>("Number of persons", WriteBenchmark.PERSONS_CNT);
        Pair<String, Long> matchedPair3 = new Pair<String, Long>("Matched", matched3);
        Pair<String, Long> elapsedTimePair3 = new Pair<String, Long>("Elapsed time(ms)", elapsedTime1);

        List<Pair> results3 = new ArrayList<Pair>();
        results3.add(benchmarkPair3);
        results3.add(keysPair3);
        results3.add(matchedPair3);
        results3.add(elapsedTimePair3);

        String fileName3 = System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter3 = new CsvFileWriter();
        csvFileWriter3.writeCsvFile(fileName3, results3);
        System.exit(0);
    }
}
