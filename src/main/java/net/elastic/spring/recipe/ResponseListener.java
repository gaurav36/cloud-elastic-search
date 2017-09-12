

package net.elastic.spring.recipe;

import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import java.io.IOException;


public interface ResponseListener {

    /**
     * Method invoked if the request yielded a successful response
     */
    void onSuccess(Response response);

    /**
     * Method invoked if the request failed. There are two main categories of failures: connection failures (usually
     * {@link java.io.IOException}s, or responses that were treated as errors based on their error response code
     * ({@link ResponseException}s).
     */
    void onFailure(Exception exception);
}
