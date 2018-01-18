
Terms
=====
Write-Ahead Log (WAL) = redo log
Durable Memory = Memory Extends


run command on imc-srv01
========================

java -cp "/home/xap/yuvald/lib/*" org.apache.ignite.benchmark.QueriesBenchmark


scp /Users/yuval/Workspace/GGvsGSbenchmark/target/GGvsGSbenchmark-1.0-SNAPSHOT.jar xap@imc-srv01:/home/xap/yuvald/lib/


java -cp "/home/xap/yuvald/apache-ignite-2.3.0-src/modules/core/target/ignite-core-2.3.0.jar:/home/xap/yuvald/apache-ignite-2.3.0-src/modules/spring/target/ignite-spring-2.3.0.jar:/home/xap/yuvald/apache-ignite-2.3.0-src/modules/indexing/target/ignite-indexing-2.3.0.jar:/home/xap/yuvald/GGvsGSbenchmark/target/GGvsGSbenchmark-1.0-SNAPSHOT.jar:/home/xap/.m2/repository/org/springframework/spring-beans/4.3.7.RELEASE/spring-beans-4.3.7.RELEASE.jar:/home/xap/.m2/repository/org/springframework/spring-core/4.3.7.RELEASE/spring-core-4.3.7.RELEASE.jar:/home/xap/.m2/repository/org/springframework/spring-context/4.3.7.RELEASE/spring-context-4.3.7.RELEASE.jar:/home/xap/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/home/xap/.m2/repository/org/springframework/spring-expression/4.3.7.RELEASE/spring-expression-4.3.7.RELEASE.jar:/home/xap/.m2/repository/javax/cache/cache-api/1.0.0/cache-api-1.0.0.jar:/home/xap/.m2/repository/com/h2database/h2/1.4.195/h2-1.4.195.jar:/home/xap/.m2/repository/org/springframework/spring-aop/4.3.7.RELEASE/spring-aop-4.3.7.RELEASE.jar" org.apache.ignite.benchmark.QueriesBenchmark


scp -r /Users/yuval/Workspace/GGvsGSbenchmark/ xap@imc-srv01:/home/xap/yuvald/

review with Niv
===============
Hazelcast and GG are working with jcache api, so it will be easy to test Hazelcast if using jcache
work with at least 2 nodes
work with backup
verify that QueriesBenchmark.scanQuery actually work with MyPerson indexes
check the used heap size and the used off heap size.
check the used disk size

docs links
==========
https://apacheignite.readme.io/docs/primary-and-backup-copies#section-configuring-backups
https://apacheignite.readme.io/v1.1/docs/off-heap-memory
https://apacheignite.readme.io/docs/durable-memory-tuning
https://apacheignite.readme.io/docs/clients-vs-servers


