package com.revilosoft.web.backend.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public final class KingBackendServer {
	
	private HttpServer httpServer;
	
	public KingBackendServer(int port) throws IOException {
		this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
		httpServer.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
	}
	
	public void addHandler(String path, HttpHandler handler) {
		HttpContext context = httpServer.createContext(path);
		context.setHandler(handler);
	}
	
	public synchronized void start() {
		this.httpServer.start();
	}
	
}
