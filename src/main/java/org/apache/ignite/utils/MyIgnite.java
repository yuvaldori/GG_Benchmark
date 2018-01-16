package org.apache.ignite.utils;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class MyIgnite {
    private static Ignite igniteInstance = null;
    protected MyIgnite() {
        // Exists only to defeat instantiation.
    }
    public static Ignite getInstanceAndStart() {
        if(igniteInstance == null) {
            igniteInstance = Ignition.start("config.xml");
            igniteInstance.active(true);
        }
        // Starting the node.
        return igniteInstance;
    }
}
