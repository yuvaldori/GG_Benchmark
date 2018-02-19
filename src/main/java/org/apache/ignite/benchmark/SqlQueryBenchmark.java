package org.apache.ignite.benchmark;

import javafx.util.Pair;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.model.MyPerson;
import org.apache.ignite.utils.CsvFileWriter;
import org.apache.ignite.utils.MyIgnite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    The format of SqlQuery is very similar to XAP SQLQuery.
    With Ignite the user must create a table on for each type.
    The creation is done on H2 Database Engine which is part on Ignite package.
 */
public class SqlQueryBenchmark {
    /*
    SqlQuery is useful for scenarios when at the end of a query execution you need to get the whole object back in the result set.
     */
    public static long runSqlQuery() {

        Ignite ignite = MyIgnite.start("client");

        IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");

        SqlQuery sql = new SqlQuery(MyPerson.class, "salary > ?");

        // Find all persons earning more than 1,000.
        QueryCursor<Map.Entry<Long, MyPerson>> cursor = null;
        try {
            cursor = cache.query(sql.setArgs(1000));
//            for (Map.Entry<Long, MyPerson> e : cursor) {
//                System.out.println(e.getValue().toString());
//            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        if (cursor != null)
            return cursor.getAll().size();
        else
            return 0;
    }

    /*
    Instead of selecting the whole object, you can choose to select only specific fields in order to minimize network and serialization overhead.
    For this purpose, Ignite implements a concept of fields queries.
    SqlFieldsQuery accepts a standard SQL query as its constructor​ parameter and executes it.
     */
    public static long runSqlFieldsQueries(){
        Ignite ignite = MyIgnite.start("client");

        IgniteCache<Long, MyPerson> cache = ignite.getOrCreateCache("MyPerson");
        // Execute query to get names of all employees.
        SqlFieldsQuery sql = new SqlFieldsQuery(
                "select concat(firstName, ' ', lastName) from MyPerson");
        QueryCursor<List<?>> cursor = null;
        // Iterate over the result set.
        try {
             cursor = cache.query(sql);

            for (List<?> row : cursor)
                System.out.println("personName=" + row.get(0));
        } catch (Exception e) {
            System.out.println(e);
        }

        if (cursor != null)
            return cursor.getAll().size();
        else
            return 0;
    }

    public static void main(String[] args){

        Long start = System.currentTimeMillis();
        long matched = SqlQueryBenchmark.runSqlQuery();
        Long end = System.currentTimeMillis();
        Long elapsedTime = end - start;

        Pair<String, String> benchmarkPair = new Pair<String, String>("Benchmark", "SqlQuery/runSqlQuery()");
        Pair<String, Long> matchedPair = new Pair<String, Long>("Matched", matched);
        Pair<String, Long> elapsedTimePair = new Pair<String, Long>("Elapsed time", elapsedTime);

        List<Pair> results = new ArrayList<Pair>();
        results.add(benchmarkPair);
        results.add(matchedPair);
        results.add(elapsedTimePair);

        String fileName = System.currentTimeMillis() + ".csv";
        CsvFileWriter csvFileWriter = new CsvFileWriter();
        csvFileWriter.writeCsvFile(fileName, results);
        System.exit(0);
    }
}