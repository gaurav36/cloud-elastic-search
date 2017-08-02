package net.elastic.spring.recipe;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.net.UnknownHostException;


/**
 */
public class IndexRecipesApp {
    public static TransportClient getTransportClient () throws UnknownHostException {
	try {
		// create client for localhost es
		TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
					 .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		return client;
	} catch (Exception e) {
		e.printStackTrace();
        }
	return null;
    }
}
