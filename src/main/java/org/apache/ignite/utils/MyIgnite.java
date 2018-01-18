package org.apache.ignite.utils;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

public class MyIgnite {

    private static Ignite igniteClient = null;
    private static Ignite igniteServer = null;

    protected MyIgnite() {
        // Exists only to defeat instantiation.
    }

    public static Ignite start(String mode) {

        if (mode.equals("client") && igniteClient == null) {
            Ignition.setClientMode(true);
            igniteClient = Ignition.start("config.xml");
            igniteClient.active(true);
            return igniteClient;
        }else if (mode.equals("client") && igniteClient != null) {
            return igniteClient;
        }else if (mode.equals("server") && igniteServer == null){
            igniteServer = Ignition.start("config.xml");
            igniteServer.active(true);
            return igniteServer;
        }else if (mode.equals("server") && igniteServer != null){
            return igniteServer;
        }else{
            return null;
        }
    }

    public static void main(String[] args) {
        MyIgnite.start("server");
    }
}
