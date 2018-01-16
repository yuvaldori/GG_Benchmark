package org.apache.ignite.utils;

import org.apache.ignite.Ignite;

import java.sql.*;

/*
Apache Ignite supports Data Definition Language (DDL) statements for creating and removing SQL tables and indexes
at runtime and Data Manipulation Language (DML) for performing queries.
Both native Apache Ignite SQL APIs, as well as JDBC and ODBC drivers, ​can be used.
The following examples will use a schema consisting of two tables.
These tables are used to hold information about a city and the people that live there.
The assumption is that a city may have many people and people will live in only one city.
This is a one-to-many (1:m) relationship.
 */
public class MySQLApp {

    static Connection conn = null;

    public static void initConnection(){
        // Starting the node.
        Ignite ignite = MyIgnite.getInstanceAndStart();

        try {
            // Register JDBC driver.
            Class.forName("org.apache.ignite.IgniteJdbcThinDriver");

            // Open JDBC connection.
            conn = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1/");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    /*
    Once the CREATE TABLE command is executed, the following happens:
    A new distributed cache is created automatically using the name of the table as its name.
    The caches store objects of type city and person that can correspond to specific Java, .NET, C++ class or Binary Object objects.
    An SQL table with all the parameters set will be defined.
    The data will be stored in key-value records.
    The primary key column will be used as an object's key and the remaining fields will belong to the value.
    This means that you can also work with the data using the key-value APIs.

    In REPLICATED mode, all the data is replicated to every node in the cluster.
    This cache mode provides the utmost availability of data as it is available on every node.
    However, in this mode every data updates must be propagated to all other nodes which can have an impact on performance and scalability.
    In Ignite, replicated caches are implemented in the way similar to partitioned caches
    where every key has a primary copy and is also backed up on all other nodes in the cluster.
    As the same data is stored on all cluster nodes, the size of a replicated cache is limited by the amount of memory (RAM and disk) available on the node.
    This mode is ideal for scenarios where cache reads are a lot more frequent than cache writes, and data sets are small.
    If your system does cache lookups over 80% of the time, then you should consider using REPLICATED cache mode.

    Cache Modes
    -----------
    https://apacheignite.readme.io/docs/cache-modes
     */
    public static void createTable(){

        try {
            // Create database tables.
            Statement stmt = conn.createStatement();

            // Create table based on REPLICATED template.
            stmt.executeUpdate("CREATE TABLE City (" +
                    " id LONG PRIMARY KEY, name VARCHAR) " +
                    " WITH \"template=replicated\"");

            // Create table based on PARTITIONED template with one backup.
            stmt.executeUpdate("CREATE TABLE MyPerson (" +
                    " id LONG, name VARCHAR, city_id LONG, " +
                    " PRIMARY KEY (id, city_id)) " +
                    " WITH \"backups=1, affinityKey=city_id\"");

            // Now, let's define several indexes in order to accelerate query lookup
            stmt.executeUpdate("CREATE INDEX idx_city_name ON City (name)");
            stmt.executeUpdate("CREATE INDEX idx_person_name ON MyPerson (name)");

        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void initData(){
        // Populate City table
        try{
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO City (id, name) VALUES (?, ?)");

            stmt.setLong(1, 1L);
            stmt.setString(2, "Forest Hill");
            stmt.executeUpdate();

            stmt.setLong(1, 2L);
            stmt.setString(2, "Denver");
            stmt.executeUpdate();

            stmt.setLong(1, 3L);
            stmt.setString(2, "St. Petersburg");
            stmt.executeUpdate();
        }catch(SQLException e) {
            System.out.println(e);
        }

            // Populate MyPerson table
            try{
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO MyPerson (id, name, city_id) VALUES (?, ?, ?)");

                stmt.setLong(1, 1L);
                stmt.setString(2, "John Doe");
                stmt.setLong(3, 3L);
                stmt.executeUpdate();

                stmt.setLong(1, 2L);
                stmt.setString(2, "Jane Roe");
                stmt.setLong(3, 2L);
                stmt.executeUpdate();

                stmt.setLong(1, 3L);
                stmt.setString(2, "Mary Major");
                stmt.setLong(3, 1L);
                stmt.executeUpdate();

                stmt.setLong(1, 4L);
                stmt.setString(2, "Richard Miles");
                stmt.setLong(3, 2L);
                stmt.executeUpdate();
            }catch (SQLException e) {
                System.out.println(e);
            }
        }

        public static boolean isTableExist(String table){
            try{
                DatabaseMetaData dbm = conn.getMetaData();
                // check if table exist
                ResultSet tables = dbm.getTables(null, null, table, null);
                if (tables.next()) {
                    return true;
                }
                else {
                    return false;
                }
            }catch(Exception e){
                System.out.println(e);
            }
            return false;
        }

        public static void dropTable(String table){
            try {
                // Create database tables.
                Statement stmt = conn.createStatement();

                // Create table based on REPLICATED template.
                stmt.executeUpdate("DROP TABLE " + table);
            }catch(Exception e){
                System.out.println(e);
            }
        }

    public static void createPersonTable(){

        try {
            // Create database tables.
            Statement stmt = conn.createStatement();

            // Create table based on PARTITIONED template with one backup.
            stmt.executeUpdate("CREATE TABLE MyPerson (" +
                    " id LONG PRIMARY KEY, orgId LONG, firstName VARCHAR, lastName VARCHAR, resume VARCHAR, salary LONG)");

        }catch(Exception e){
            System.out.println(e);
        }

        // Populate MyPerson table
        try{
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO MyPerson (id, orgId, firstName, lastName, resume, salary) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, 1L);
            stmt.setLong(2, 2L);
            stmt.setString(3, "Yuval");
            stmt.setString(4, "Dori");
            stmt.setString(5, "Master Degree");
            stmt.setLong(6, 100000L);
            stmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e);
        }
    }

        public static void main(String[] args){
            MySQLApp.initConnection();
            System.out.println("Creating tables...");
            MySQLApp.createTable();
            System.out.println("Inserting data");
            MySQLApp.initData();
            System.out.println("Tables CITY and PERSON has been created with data");
        }
    }

