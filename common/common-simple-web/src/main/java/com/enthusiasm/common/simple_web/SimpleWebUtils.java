package com.enthusiasm.common.simple_web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

public class SimpleWebUtils {

    private static int DEFAULT_BACKLOG = 0;

    private SimpleWebUtils() {
    }

    public static void start(int port, ExecutorService executor) throws IOException {
        HttpServer webServer = HttpServer.create(new InetSocketAddress(port), DEFAULT_BACKLOG);
        webServer.createContext("/health", exchange -> {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        });
        webServer.setExecutor(executor);
        webServer.start();
    }
}
