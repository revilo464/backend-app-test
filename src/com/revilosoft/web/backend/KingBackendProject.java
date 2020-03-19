package com.revilosoft.web.backend;

import java.io.IOException;

import com.revilosoft.web.backend.handlers.DefaultRequestHandler;
import com.revilosoft.web.backend.server.KingBackendServer;

public class KingBackendProject {
	
	static int port = 8080;
	static boolean isDebugEnabled = false;
	
	public static void main(String[] args) throws IOException {
		for (String s: args) {
            String[] split = s.split("=");
            
            if (split.length != 2) {
            	throw new UnsupportedOperationException("All params must be in the form <param>=<value>");
            }
            
            switch (split[0]) {
            	case "port":
            		port = Integer.parseInt(split[1]);
            		break;
            	case "debug":
            		isDebugEnabled = Boolean.parseBoolean(split[1]);
            		break;
            	default:
            		throw new UnsupportedOperationException("unsupported param: " + split[0]);
            }
        }
		
		KingBackendServer server = new KingBackendServer(port);
		server.addHandler("/", new DefaultRequestHandler(isDebugEnabled));
		server.start();
	}

}
