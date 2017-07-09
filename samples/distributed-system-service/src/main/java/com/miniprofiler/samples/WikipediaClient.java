package com.miniprofiler.samples;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Client to make requests to Wikipedia.
 */
public class WikipediaClient {
    private static String WIKIPEDIA_REQUEST_URL =
            "https://en.wikipedia.org/w/api.php?action=query&list=random&rnnamespace=0&rnlimit=1&format=json";

    /**
     * Returns either a random English Wikipedia article title or null in case of an error.
     */
    public String getRandomWikipediaArticleName() throws IOException {
        // Get response from Wikipedia.
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(WIKIPEDIA_REQUEST_URL);
        String responseContent = "";
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                return null;
            }
            responseContent = IOUtils.toString(response.getEntity().getContent());
        } finally {
            httpGet.releaseConnection();
        }
        // Parse response.
        ObjectMapper mapper = new ObjectMapper();
        WikipediaResponse wikipediaResponse = mapper.readValue(responseContent, WikipediaResponse.class);
        return wikipediaResponse.query.random.get(0).title;
    }

    /*
        {
            "batchcomplete": "",
            "continue": {
                "rncontinue": "0.284646709280|0.284647818375|31090907|0",
                "continue": "-||"
            },
            "query": {
                "random": [
                    {
                        "id": 53693569,
                        "ns": 0,
                        "title": "First National Bank of Ekalaka and Rickard Hardware Store Building"
                    }
                ]
            }
        }
    */
    private static class WikipediaResponse {
        static class ContinuationToken {
            @JsonProperty("rncontinue") String randomContinueToken;
            @JsonProperty("continue") String continueToken;
        }

        static class Item {
            @JsonProperty("id") long id;
            @JsonProperty("ns") long nameSpace;
            @JsonProperty("title") String title;
        }

        static class Query {
            @JsonProperty("random")
            List<Item> random;
        }

        @JsonProperty("batchcomplete") String batchComplete;
        @JsonProperty("continue") ContinuationToken continuationToken;
        @JsonProperty("query") Query query;
    }
}
