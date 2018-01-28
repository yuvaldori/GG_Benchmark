package org.apache.ignite.benchmark;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.model.MyPerson;

import java.util.Map;

public class WriteThread extends Thread{

    public static IgniteCache<Long, MyPerson> cache;
    Map<Long, MyPerson> personsMap = null;

    public WriteThread(Map<Long, MyPerson> personsMap){
        this.personsMap = personsMap;
    }

    @Override
    public void run(){
        System.out.println("WriteThread: Start writing...");
        DataBuilder.cache.putAll(personsMap);
        System.out.println("WriteThread: writing has been finished ");
    }
}
