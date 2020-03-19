package com.revilosoft.web.backend.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.revilosoft.web.backend.handlers.DefaultRequestHandler.Action;

@RunWith(JUnitPlatform.class)
class DefaultRequestHandlerTest {
	
	@Test
	void testGetRandomSessionId() {
		String uuid = DefaultRequestHandler.getRandomSessionId();
		assertEquals(8, uuid.length());
		assertEquals(true, uuid.matches("\\w*"));
	}
	
	@Test
	void testGetPairs() throws UnsupportedEncodingException, URISyntaxException {
		URI uri = new URI("http://www.test.com/test?test=resp");
		Map<String, String> map = DefaultRequestHandler.splitQuery(uri);
		assertEquals("resp", map.get("test"));
	}
	
	@Test
	void testWrongParts() throws URISyntaxException {
		URI uri = new URI("http://www.test.com/134");
		try {
			@SuppressWarnings("unused")
			Action action = DefaultRequestHandler.getActionAndParams(uri);
		} catch (Exception e) {
			assertEquals("incorrect parts in URI", e.getMessage());
		}
	}
	
	@Test
	void testFirstPartWrong() throws URISyntaxException {
		URI uri = new URI("http://www.test.com/34jkj/login");
		try {
			@SuppressWarnings("unused")
			Action action = DefaultRequestHandler.getActionAndParams(uri);
		} catch (Exception e) {
			assertEquals("first path param is not a number", e.getMessage());
		}
	}
	
	@Test
	void testSessionIdInvalid() throws URISyntaxException {
		URI uri = new URI("http://www.test.com/34/score?sessionkey=6GH28&&&");
		try {
			@SuppressWarnings("unused")
			Action action = DefaultRequestHandler.getActionAndParams(uri);
		} catch (Exception e) {
			assertEquals("session ID contains non-word characters", e.getMessage());
		}
	}
	
	@Test
	void testInvalidAction() throws URISyntaxException {
		URI uri = new URI("http://www.test.com/34/testaction?sessionkey=6GH28&&&");
		try {
			@SuppressWarnings("unused")
			Action action = DefaultRequestHandler.getActionAndParams(uri);
		} catch (Exception e) {
			assertEquals("invalid action type", e.getMessage());
		}
	}

}
