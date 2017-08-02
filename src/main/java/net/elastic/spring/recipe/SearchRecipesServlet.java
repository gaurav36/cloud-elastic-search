package net.elastic.spring.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * Created by Gaurav Garg on 12/07/17.
 */

public class SearchRecipesServlet {

    //private static final Logger logger = LoggerFactory.getLogger(ElasticSearchQueries.class);

    public SearchHits findInIndex(String index, String key, String value) {
        try {
            //SearchResponse response = ElasticSearchUtil.getClient().prepareSearch(index)
            SearchResponse response = IndexRecipesApp.getTransportClient().prepareSearch(index)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.termQuery(key, value)) // Query
                    //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18)) // Filter
                    .setFrom(0).setSize(60).setExplain(true)
                    .execute()
                    .actionGet();
            return response.getHits();
        } catch (Exception e) {
		e.printStackTrace();
        }
        return null;
    }

    /*public SearchHits findByQuery(QueryBuilder builder) {
        try {
            SearchResponse response = ElasticSearchUtil.getClient().prepareSearch()
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(builder) // Query
                    .execute()
                    .actionGet();
            return response.getHits();
        } catch (Exception exception) {
            logger.error(" findByQuery ", exception);
        }
        return null;
    }*/

    /*public MultiSearchResponse multiSearch(List<SearchRequestBuilder> searchRequestList) {
        try {
            MultiSearchRequestBuilder builder = ElasticSearchUtil.getClient().prepareMultiSearch();
            for (SearchRequestBuilder requestBuilder : searchRequestList) {
                builder.add(requestBuilder);
            }
            return builder.execute().actionGet();
        } catch (Exception e) {

        }
        return null;
    }*/

}
