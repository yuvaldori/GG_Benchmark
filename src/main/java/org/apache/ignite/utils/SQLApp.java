package org.apache.ignite.utils;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.MySQLDialect;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.model.Contact;
import org.apache.ignite.model.Gender;
import org.apache.ignite.model.Person;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
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
public class SQLApp {

    static Connection conn = null;

    public static void initConnection(){

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
    public static void createTable() {

        try {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE person (" +
                    " id int(11) NOT NULL, " +
                    " first_name varchar(45) , " +
                    " gender varchar(10) , " +
                    " country varchar(10) , " +
                    " city varchar(20) , " +
                    " address varchar(45) , " +
                    " birth_date date , " +
                    " PRIMARY KEY (id))");

            stmt.executeUpdate("CREATE TABLE contact (" +
                    " id int(11) NOT NULL, " +
                    " location varchar(45) , " +
                    " contact_type varchar(10) , " +
                    " person_id int(11) NOT NULL, " +
                    " PRIMARY KEY (id))");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void alterTable() {
        try {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("ALTER TABLE ignite.contact ADD INDEX person_fk_idx (person_id ASC)");
            stmt.executeUpdate("ALTER TABLE ignite.contact ADD CONSTRAINT person_fk FOREIGN KEY (person_id) REFERENCES ignite.person (id) ON DELETE CASCADE ON UPDATE CASCADE");
        }catch(SQLException e) {
            System.out.println(e);
        }
    }

    @Bean
    public static Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("ignite-1");
        cfg.setPeerClassLoadingEnabled(true);

        CacheConfiguration<Long, Contact> ccfg2 = new CacheConfiguration<>("ContactCache"); // (1)
        ccfg2.setIndexedTypes(Long.class, Contact.class); // (2)
        ccfg2.setWriteBehindEnabled(true);
        ccfg2.setWriteThrough(true); // (3)
        ccfg2.setReadThrough(true); // (4)
        CacheJdbcPojoStoreFactory<Long, Contact> f2 = new CacheJdbcPojoStoreFactory<>(); // (5)
        //f2.setDataSource(datasource); // (6)
        f2.setDialect(new MySQLDialect()); // (7)
        JdbcType jdbcContactType = new JdbcType(); // (8)
        jdbcContactType.setCacheName("ContactCache");
        jdbcContactType.setKeyType(Long.class);
        jdbcContactType.setValueType(Contact.class);
        jdbcContactType.setDatabaseTable("contact");
        jdbcContactType.setDatabaseSchema("ignite");
        jdbcContactType.setKeyFields(new JdbcTypeField(Types.INTEGER, "id", Long.class, "id"));
        jdbcContactType.setValueFields(new JdbcTypeField(Types.VARCHAR, "contact_type", String.class, "type"), new JdbcTypeField(Types.VARCHAR, "location", String.class, "location"), new JdbcTypeField(Types.INTEGER, "person_id", Long.class, "personId"));
        f2.setTypes(jdbcContactType);
        ccfg2.setCacheStoreFactory(f2);

        CacheConfiguration<Long, Person> ccfg = new CacheConfiguration<>("PersonCache");
        ccfg.setIndexedTypes(Long.class, Person.class);
        ccfg.setWriteBehindEnabled(true);
        ccfg.setReadThrough(true);
        ccfg.setWriteThrough(true);
        CacheJdbcPojoStoreFactory<Long, Person> f = new CacheJdbcPojoStoreFactory<>();
        //f.setDataSource(datasource);
        f.setDialect(new MySQLDialect());
        JdbcType jdbcType = new JdbcType();
        jdbcType.setCacheName("PersonCache");
        jdbcType.setKeyType(Long.class);
        jdbcType.setValueType(Person.class);
        jdbcType.setDatabaseTable("person");
        jdbcType.setDatabaseSchema("ignite");
        jdbcType.setKeyFields(new JdbcTypeField(Types.INTEGER, "id", Long.class, "id"));
        jdbcType.setValueFields(new JdbcTypeField(Types.VARCHAR, "first_name", String.class, "firstName"), new JdbcTypeField(Types.VARCHAR, "last_name", String.class, "lastName"), new JdbcTypeField(Types.VARCHAR, "gender", Gender.class, "gender"), new JdbcTypeField(Types.VARCHAR, "country", String.class, "country"), new JdbcTypeField(Types.VARCHAR, "city", String.class, "city"), new JdbcTypeField(Types.VARCHAR, "address", String.class, "address"), new JdbcTypeField(Types.DATE, "birth_date", Date.class, "birthDate"));
        f.setTypes(jdbcType);
        ccfg.setCacheStoreFactory(f);

        cfg.setCacheConfiguration(ccfg, ccfg2);

        return Ignition.start(cfg);
    }


    public static void main(String[] args){
        SQLApp.igniteInstance();
        SQLApp.initConnection();
        System.out.println("Creating tables...");
        SQLApp.createTable();
        System.out.println("Tables has been created");
    }
}

