package com.revilosoft.web.backend.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

class KingBackendServerTest {

	@Test
	void testPortSet() throws Exception {
		KingBackendServer server = new KingBackendServer(8080);
		var serverField = KingBackendServer.class.getDeclaredField("httpServer");
		serverField.setAccessible(true);
		HttpServer serverImpl = (HttpServer) serverField.get(server);
		assertEquals(8080, serverImpl.getAddress().getPort());
	}

}
