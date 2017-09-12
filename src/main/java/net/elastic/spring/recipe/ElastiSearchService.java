package net.elastic.spring.recipe;

import java.io.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import net.elastic.spring.dto.QueryIndexer;

import net.elastic.spring.recipe.ElasticSearchRequest;
import net.elastic.spring.recipe.IndexRecipesApp;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.action.index.IndexRequest;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import org.elasticsearch.client.Response;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.concurrent.CountDownLatch;
//import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
//import org.elasticsearch.client.http.nio.protocol.HttpAsyncResponseConsumer;
//import org.elasticsearch.client.ResponseListener;
//import org.elasticsearch.client.http.nio.protocol.HttpAsyncResponseConsumer;
import org.elasticsearch.client.ResponseListener;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.http.impl.nio.reactor.IOReactorConfig;


/**
 */
public class ElastiSearchService {
    private static ElastiSearchService instance = null;

    public static ElastiSearchService getInstance() {
        if (instance == null) {
            instance = new ElastiSearchService();
        }
        return instance;
    }

    public boolean isIndexExist(String id) {
        try {
            if (IndexRecipesApp.getTransportClient().admin().indices().prepareExists(id).execute().actionGet().isExists()) {
                return true;
            }
        } catch (Exception exception) {
		exception.printStackTrace();
        }

        return false;
    }

    public IndexResponse createIndex(String index, String type, String id, XContentBuilder jsonData) {
        IndexResponse response = null;
        try {
            response = IndexRecipesApp.getTransportClient().prepareIndex(index, type, id)
                    .setSource(jsonData)
                    .get();
            Thread.sleep(2000);
            return response;
        } catch (Exception e) {
		e.printStackTrace();
        }
        return null;
    }

    public GetResponse findDocumentByIndex(String index, String type, String id) {
        try {
            GetResponse getResponse = IndexRecipesApp.getTransportClient().prepareGet(index, type, id).get();
            return getResponse;
        } catch (Exception e) {
		e.printStackTrace();
        }
        return null;
    }
    
    public SearchResponse findDocument(String index, String type, String field, String value) {
        try {
            QueryBuilder queryBuilder = new MatchQueryBuilder(field, value);
            SearchResponse response   = IndexRecipesApp.getTransportClient().prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.QUERY_AND_FETCH)
                    .setQuery(queryBuilder)
                    .setFrom(0).setSize(60).setExplain(true)
                    .execute()
                    .actionGet();
            SearchHit[] results = response.getHits().getHits();
            return response;
        } catch (Exception e) {
            //logger.error("", e);
            System.out.println ("Debug: findDocumentByValue error: " + e);
        }
        return null;
    }

    public UpdateResponse updateIndex(String index, String type, String id, XContentBuilder jsonData) {
        UpdateResponse response = null;
        try {
            System.out.println("updateIndex ");
            response = IndexRecipesApp.getTransportClient().prepareUpdate(index, type, id)
                    .setDoc(jsonData)
                    //.execute().get();
                    .get();
	            //.setRefresh(true).execute().actionGet();
            System.out.println("Debug: logging response is: " + response);
            return response;
        } catch (Exception e) {
            //logger.error("UpdateIndex", e);
            System.out.println("Debug: error in UpdateIndex:  " + e);
        }
        return null;
    }

    public DeleteResponse removeDocument(String index, String type, String id) {
        DeleteResponse response = null;
        try {
            response = IndexRecipesApp.getTransportClient().prepareDelete(index, type, id).execute().actionGet();
            return response;
        } catch (Exception e) {
            //logger.error("RemoveIndex", e);
        }
        return null;
    }

    public static boolean createPipeline(){

	RestClient requester = RestClient.builder( new HttpHost ("localhost", 9300),
                               new HttpHost ("localhost", 9200)).build();
        HttpEntity body = new NStringEntity("{ \"description\" : \"Extract attachment information\", \n" +
                "  \"processors\": [\n" +
                "    {\n" +
                "      \"attachment\": {\n" +
                "        \"field\": \"data\",\n" +
                "        \"indexed_chars\": -1\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"set\": {\n" +
                "        \"field\": \"attachment.title\",\n" +
                "        \"value\": \"{{title}}\"\n" +
                "      }\n" +
                "    }\n" +
                "  ] \n" +
                "}\n",ContentType.APPLICATION_JSON);
        try {
            requester.performRequest("PUT","/_ingest/pipeline/attachment", Collections.<String,String>emptyMap(), body);
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static String InputStreamToString(InputStream in) {   
        try {
            String response = ""; 

            BufferedReader nin = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String sCurrentLine;
            while ((sCurrentLine = nin.readLine()) != null) {
                response +=sCurrentLine;

            }
            return response;
        }
            catch (Exception e) {
            return ""; 
        }
    }

    RestClient requester;

    public void RestClientBuilder() {
       requester = RestClient.builder( new HttpHost ("192.168.178.79", 9300),
                                       new HttpHost ("192.168.178.79", 9200)).setHttpClientConfigCallback(
                                       new RestClientBuilder.HttpClientConfigCallback() {
                                   @Override
                                   public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                      return httpClientBuilder.setDefaultIOReactorConfig(
                                           IOReactorConfig.custom().setIoThreadCount(1).build());
                                   }
                               })
                               .build();
    }

    public void RestClientCleanUp() {
        System.out.println ("Closing restclient resources....");
        try {
            requester.close();
            System.out.println ("restclient resources cleaned up....");
        } catch (Exception e) {
            e.printStackTrace();
       }
    }

    
    public void IndexComputeController (QueryIndexer computeIndex) {
	    byte[] wikiArray;
	    String str = null;
	    IndexResponse response = null;

	    String filePath = computeIndex.getfilePath();
	    String index    = computeIndex.getindex();
	    Long   id       = computeIndex.getId();

	    System.out.println ("inside elastic search controller: " + filePath);
            System.setProperty("io.netty.allocator.type", "unpooled");

	    // Creating pipeline for ingest attachment plugin
	    //createPipeline ();

	    //final CountDownLatch latch = new CountDownLatch(1);

	    try {
		/*RestClient requester = RestClient.builder( new HttpHost ("localhost", 9300),
				                           new HttpHost ("localhost", 9200)).build();*/

		HttpEntity body = new NStringEntity("{" +
				"\"data\":\"" + encoder(filePath) + "\"" + ",\n" +
				"\"title\":\"" + filePath + "\"" +
				"}", ContentType.APPLICATION_JSON);
		HashMap<String,String> param = new HashMap<String,String>();
		param.put("pipeline", "attachment");

		/*ResponseListener listner = 		                                       new ResponseListener() {
									       @Override
									       public void onSuccess(Response response) {
										       System.out.println(response);
										       latch.countDown();
									       }

									       @Override
									       public void onFailure(Exception exception) {
										       latch.countDown();
									       }
		                                                     };*/
		Response httpresponse = requester.performRequest("PUT", "/"+index+"/"+"doc"+"/"+id, param, body);
		//Response httpresponse = requester.performRequestAsync("PUT", "/"+index+"/"+"doc"+"/"+id, param, body);
		//Response httpresponse = requester.performRequestAsync("PUT", "/"+index+"/"+"doc"+"/"+id, param, body, null
		//Response httpresponse = requester.performRequestAsync("PUT", "/"+index+"/"+"doc"+"/"+id, param, body, listner
		//requester.performRequestAsync("PUT", "/"+index+"/"+"doc"+"/"+id, param, body, listner);

		//latch.await();
		//System.out.println ("POST response is: " + httpresponse);
		//requester.close();
	    } catch (Exception e) {
		    e.printStackTrace();
	    }
    }

    public void IndexComputeControllerSearch (String index, String searchStr) {
	try {
		RestClient requester = RestClient.builder( new HttpHost ("localhost", 9300),
                                                           new HttpHost ("localhost", 9200)).build();

                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("q", "*:*");
                paramMap.put("pretty", "true");

                /*HttpEntity body = new NStringEntity(
                                "{\"query\": {\n" +
                                "\"match_phrase\": {\n" +
                                //"\"match\": {\n" +
                                //"file.content": {
                                "\"attachment.content\": {\n" +
                                "\"query\": \"" + "Kaution" + "\",\n"
                                "}\n" +
                                "}\n" +
                                "}\n" +
                                "}", ContentType.APPLICATION_JSON);*/

                HttpEntity body = new NStringEntity(
                                    "{\"query\": {\n" +
                                            "        \"match_phrase\": {\n" +
                                            "            \"attachment.content\": {\n" +
                                            "              \"query\": \"" + searchStr + "\",\n" +
                                            "\t  \"slop\": 1\n" +
                                            "            }\n" +
                                            "        }\n" +
                                            "    }\n" +
                                            //"    },\n" +
                                            /*"    \"highlight\": {\n" +
                                            "        \"pre_tags\" : [\"<mark>\"],\n" +
                                            "        \"post_tags\" : [\"</mark>\"],\n" +
                                            "        \"fields\" : {\n" +
                                            "            \"attachment.content\" : {\n" +
                                            "                \"fragmenter\" : \"span\",\n" +
                                            "                \"boundary_scanner\" : \"sentence\"\n" +
                                            "            }\n" +
                                            "        }\n" +
                                            "    }\n  " +*/

                                            "    }", ContentType.APPLICATION_JSON);

                Response indexResponse = requester.performRequest("GET","/"+index+"/"+"doc"+"/_search", Collections.<String,String>emptyMap(), body);

                System.out.println ("DEBUG: search index response is: " + indexResponse);
                String searchresponse = InputStreamToString(indexResponse.getEntity().getContent());

                JSONObject jsonResponse = new JSONObject(searchresponse);
                int size = jsonResponse.getJSONObject("hits").getInt("total");
                System.out.println ("DEBUG:  total hit size is:" + size);

		if (size > 0) {
			for (int l = 0; l < size; l++) {
				JSONArray  Analysehits = jsonResponse.getJSONObject("hits").getJSONArray("hits");
				JSONObject hit         = jsonResponse.getJSONObject("hits").getJSONArray("hits").
					                                                    getJSONObject(l);

				String id    = hit.getString("_id");
				int    score = (int) (hit.getDouble("_score"));
				String title = Analysehits.getJSONObject(l).getJSONObject("_source").
					       getJSONObject("attachment").getString("title");

				System.out.println ("file name is: " + title);
				System.out.println ("hit score is: " + score);
				System.out.println ("hit id is: " + id);
			}
		}
        } catch (Exception e) {
		e.printStackTrace();
	}
    }

    public static String encoder(String path) throws Exception {

        String[] splitPath = path.split("\\.");
        //String[] splitPath = path.split("\\.", -1)[0];
        //String extension = splitPath[1];

        //if (!extension.equals("pdf")) {
        //    throw new UnsupportedOperationException();
        //}

        File file = new File(path);

        System.out.println ("DEBUG:: file name is: " + path + " file size is: " + file.length()/1024 + " KB");
        if (!file.exists()) {
            throw new Exception("File does not exist");
        }

        //Reading and encoding files
        byte fileContent[] = Files.readAllBytes(file.toPath());
        String result64 = javax.xml.bind.DatatypeConverter.printBase64Binary(fileContent);

        return result64;
    }

    /*public MultiGetResponse findByMultipleIndexs(List<ElasticSearchRequest> requests) {
        try {
            MultiGetRequestBuilder builder = ElasticSearchUtil.getClient().prepareMultiGet();
            for (ElasticSearchRequest _request : requests) {
                builder.add(_request.getIndex(), _request.getType(), _request.getId());
            }
            return builder.get();
        } catch (Exception e) {
            //logger.error("findByMultipleIndexs", e);
        }
        return null;
    }

    public List<String> getAlldata(MultiGetResponse multiGetResponse) {
        List<String> data = new ArrayList<>();
        try {
            for (MultiGetItemResponse itemResponse : multiGetResponse) {
                GetResponse response = itemResponse.getResponse();
                if (response.isExists()) {
                    String json = response.getSourceAsString();
                    data.add(json);
                }
            }
            return data;
        } catch (Exception e) {
            //logger.error("", e);
        }
        return null;
    }*/
}
