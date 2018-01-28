package org.apache.ignite.benchmark;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.model.MyPerson;
import org.apache.ignite.utils.MyIgnite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataBuilder {

    public static IgniteCache<Long, MyPerson> cache;

    public static Set<Long> buildKeys(long startKey, long endKey){
        Set<Long> keys = new HashSet<Long>();
        for (long i=startKey; i<endKey; i++){
            keys.add(i);
        }

        return keys;
    }

      public static Map<Long, MyPerson> buildData(long startKey, long endKey){
        // start client
        Ignite ignite = MyIgnite.start("client");
        cache = ignite.getOrCreateCache("MyPerson");
        Affinity aff = ignite.affinity("MyPerson");
        Map<Long, MyPerson> personsMap = new HashMap<Long, MyPerson>();

        for (long personId = startKey; personId < endKey; personId++) {
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

            personsMap.put(personId, myPerson);
        }

        return personsMap;
    }
}
