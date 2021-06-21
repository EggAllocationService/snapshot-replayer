package io.egg.server;

import net.minestom.server.Bootstrap;

public class BootstrapMain {
    public static void main(String[] args) {

        Bootstrap.bootstrap(Main.class.getName(), args);
    }
}
