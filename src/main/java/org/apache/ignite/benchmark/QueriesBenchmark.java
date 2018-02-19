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
    public static Map<Long, MyPerson> getByKeys(Set<Long> keys){
        // Starting the node.
        Ignite ignite = MyIgnite.start("client");
                IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");
        Map<Long, MyPerson> personsMap = cache.getAll(keys);

        return personsMap;
    }

    /*
     * Similar to XAP Projection
     * Scan query with filter.
     * Scan queries also support optional transformer closure which allows to convert the entry on the server node before sending it to the client.
     * This is useful, for example, when you want to fetch only several fields out of large object and want to minimize the network traffic.
     * Example below shows how to fetch only keys and do not send value objects at all.
     * Get only keys for persons earning more than 1,000.
     */
    public static List<Long> scanQuery(){

        // Starting the node.
        Ignite ignite = MyIgnite.start("client");
        IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");

        List<Long> keys = cache.query(new ScanQuery<Long, MyPerson>(
                        (k, p) -> p.getSalary() > 1000), // Remote filter.
                Cache.Entry::getKey              // Transformer.
        ).getAll();

        return keys;
    }

    /*
    * Constructs query for the given search string.
    */
    public static long testQueries(){

        // Starting the node.
        Ignite ignite = MyIgnite.start("client");

        IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");

        // Query for all people with "Master Degree" in their resumes.
        TextQuery txt = new TextQuery(MyPerson.class, "Master Degree");

        QueryCursor<Map.Entry<Long, MyPerson>> masters = null;

        try {
            masters = cache.query(txt);
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
        2) COUNT KEYS
         */
        Set<Long> keys1 = new HashSet<Long>();
        for (long i=1000; i<2000; i++){
            keys1.add(i);
        }
        Set<Long> keys2 = new HashSet<Long>();
        for (long i=9000000; i<9001000; i++){
            keys2.add(i);
        }
        Set<Long> keys3 = new HashSet<Long>();
        for (long i=3000; i<4000; i++){
            keys3.add(i);
        }
        Set<Long> keys4 = new HashSet<Long>();
        for (long i=9003000; i<9004000; i++){
            keys4.add(i);
        }
        Set<Long> keys5 = new HashSet<Long>();
        for (long i=5000; i<6000; i++){
            keys5.add(i);
        }

        Long start2 = System.currentTimeMillis();
        Map<Long, MyPerson> persons1 = QueriesBenchmark.getByKeys(keys1);
        Map<Long, MyPerson> persons2 = QueriesBenchmark.getByKeys(keys2);
        Map<Long, MyPerson> persons3 = QueriesBenchmark.getByKeys(keys3);
        Map<Long, MyPerson> persons4 = QueriesBenchmark.getByKeys(keys4);
        Map<Long, MyPerson> persons5 = QueriesBenchmark.getByKeys(keys5);

        Long totalMached = new Long(persons1.size() + persons2.size() + persons3.size() + persons4.size() + persons5.size());

        Long end2 = System.currentTimeMillis();
        Long elapsedTime2 = end2 - start2;

        Pair<String, String> benchmarkPair2 = new Pair<String, String>("Benchmark", "Queries/getByKeys()");
        Pair<String, Long> keysPair2 = new Pair<String, Long>("Number of keys", 5000L);
        Pair<String, Long> matchedPair2 = new Pair<String, Long>("Matched",totalMached);
        Pair<String, Long> elapsedTimePair2 = new Pair<String, Long>("Elapsed time(ms)", elapsedTime2);

        List<Pair> results2 = new ArrayList<Pair>();
        results2.add(benchmarkPair2);
        results2.add(keysPair2);
        results2.add(matchedPair2);
        results2.add(elapsedTimePair2);

        String fileName2 = "GG" + System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter2 = new CsvFileWriter();
        csvFileWriter2.writeCsvFile(fileName2, results2);


        /*
        3) COUNT WHERE SALARY > 1000
         */
        Long start3 = System.currentTimeMillis();
        List<Long> keysList = QueriesBenchmark.scanQuery();
        Long end3 = System.currentTimeMillis();
        Long elapsedTime1 = end3 - start3;

        Pair<String, String> benchmarkPair3 = new Pair<String, String>("Benchmark", "Queries/scanQuery()");
        Pair<String, Long> keysPair3 = new Pair<String, Long>("Number of persons", 5000L);
        Pair<String, Long> matchedPair3 = new Pair<String, Long>("Matched", new Long(keysList.size()));
        Pair<String, Long> elapsedTimePair3 = new Pair<String, Long>("Elapsed time(ms)", elapsedTime1);

        List<Pair> results3 = new ArrayList<Pair>();
        results3.add(benchmarkPair3);
        results3.add(keysPair3);
        results3.add(matchedPair3);
        results3.add(elapsedTimePair3);

        String fileName3 = "GG" + System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter3 = new CsvFileWriter();
        csvFileWriter3.writeCsvFile(fileName3, results3);
        System.exit(0);
    }
}
