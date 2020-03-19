package com.revilosoft.web.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class KingBackendProjectTest {

	@Test
	void testPort() throws IOException {
		KingBackendProject.main(new String[] { "port=8082" } );
		assertEquals(8082, KingBackendProject.port);
	}
	
	@Test
	void testDebug() throws IOException {
		KingBackendProject.main(new String[] { "debug=true" } );
		assertEquals(true, KingBackendProject.isDebugEnabled);
	}
}
