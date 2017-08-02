
import java.util.Map;
import java.util.HashMap;
import org.testng.Assert;
import java.io.IOException;
import org.testng.annotations.Test;
import java.net.UnknownHostException;
import org.elasticsearch.common.Strings;
import org.testng.annotations.BeforeTest;
import org.elasticsearch.search.SearchHit;
import net.elastic.spring.recipe.IndexRecipesApp;
import net.elastic.spring.recipe.ElastiSearchService;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/*
 *
 * @author Gaurav Garg
 */

public class ElasticSearchCRUDTest {

    ElastiSearchService elastiSearchService = null;

    @BeforeTest
    public void init() throws UnknownHostException {
        elastiSearchService = ElastiSearchService.getInstance();
    }


    @Test(enabled = true, priority = 1)
    public void createIndex() throws IOException {
        XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("FirstName", "Gaurav");
        data.put("LastName", "Garg");
        data.put("elastic", "search");
        jsonBuilder.map(data);
        IndexResponse indexResponse = elastiSearchService.createIndex("users", "gg", "1", jsonBuilder);
        IndexResponse indexResponse2 = elastiSearchService.createIndex("gaurav", "kumar", "2", jsonBuilder);
	System.out.println ("DEBUG: index response is: " + indexResponse);

	// If you create index first time it will give you "OK" as a status output but after deleting
	// index and recreating same index again it will give you "CREATED" status response.
	// TO DO: compare these value in actual assert test case instead of this hacky way
	boolean value = new String(indexResponse.status().toString()).equals("OK") ||
	                new String(indexResponse.status().toString()).equals("CREATED");
		        
        Assert.assertSame(value, true);
    }

    @Test(enabled = true, priority = 2)
    public void findDocumentByIndex() {
        GetResponse response = elastiSearchService.findDocumentByIndex("users", "gg", "1");
        Map<String, Object> source = response.getSource();
        System.out.println("------------------------------");
        System.out.println("Index: " + response.getIndex());
        System.out.println("Type: " + response.getType());
        System.out.println("Id: " + response.getId());
        System.out.println("Version: " + response.getVersion());
        System.out.println("getFields: " + response.getFields());
        System.out.println(source);
        System.out.println("------------------------------");
        Assert.assertSame(response.isExists(), true);
    }

    
    @Test(enabled = true, priority = 3)
    public void findDocumentByValue() {
        SearchResponse response = elastiSearchService.findDocument("users", "gg", "LastName", "Garg");
        SearchHit[] results = response.getHits().getHits();
        for (SearchHit hit : results) {
            System.out.println("--------------HIT----------------");
            System.out.println("Index: " + hit.getIndex());
            System.out.println("Type: " + hit.getType());
            System.out.println("Id: " + hit.getId());
            System.out.println("Version: " + hit.getVersion());
            Map<String, Object> result = hit.getSource();
            System.out.println(result);
        }
        Assert.assertSame(response.getHits().totalHits() > 0, true);
    }

    
    @Test(enabled = true, priority = 4)
    public void UpdateDocument() throws IOException {
        XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("FirstName", "Updated Gaurav");
        data.put("LastName", "Updated TEST");
        jsonBuilder.map(data);
        UpdateResponse updateResponse = elastiSearchService.updateIndex("users", "gg", "1", jsonBuilder);
	// Refreshing all indices to get fresh updated result
	IndexRecipesApp.getTransportClient().admin().indices().prepareRefresh().get();
        SearchResponse response = elastiSearchService.findDocument("users", "gg", "LastName", "Updated TEST");
        Assert.assertSame(response.getHits().totalHits() > 0, true);
    }

    
    @Test(enabled = true, priority = 5)
    public void RemoveDocument() throws IOException {
        DeleteResponse deleteResponse = elastiSearchService.removeDocument("users", "gg", "1");
        Assert.assertSame(deleteResponse.status().toString(), "OK");

    }
}
