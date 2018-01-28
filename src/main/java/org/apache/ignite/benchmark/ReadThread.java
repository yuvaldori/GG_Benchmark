package org.apache.ignite.benchmark;

import java.util.Set;

public class ReadThread extends Thread{

    Set<Long> keys;

    public ReadThread(Set<Long> keys){
        this.keys = keys;
    }

    @Override
    public void run(){
        System.out.println("ReadThread: Start reading...");
        DataBuilder.cache.getAll(keys);
        System.out.println("ReadThread: reading has been finished ");
    }
}
