package com.miniprofiler.samples;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.miniprofiler.Closeable;
import com.miniprofiler.MiniProfiler;

public class IndexServlet extends HttpServlet {
    private static String WIKIPEDIA_REQUEST_URL =
            "https://en.wikipedia.org/w/api.php?action=query&list=random&rnnamespace=0&rnlimit=1&format=json";
    private static String WIKIPEDIA_ARTICLE_URL = "https://en.wikipedia.org/wiki/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<String> randomArticleTitle = getRandomWikipediaArticleName();
        request.setAttribute("article", buildArticleRepresentation(randomArticleTitle));
        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
    }

    private Optional<String> getRandomWikipediaArticleName() throws IOException {
        MiniProfiler profiler = MiniProfiler.getCurrent();
        // Get response from Wikipedia.
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(WIKIPEDIA_REQUEST_URL);
        String responseContent = "";
        try (Closeable step = profiler.step("Call Wikipedia");
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                return Optional.empty();
            }
            responseContent = IOUtils.toString(response.getEntity().getContent());
        } finally {
            httpGet.releaseConnection();
        }
        // Parse response.
        ObjectMapper mapper = new ObjectMapper();
        WikipediaResponse wikipediaResponse = mapper.readValue(responseContent, WikipediaResponse.class);
        return Optional.of(wikipediaResponse.query.random.get(0).title);
    }

    private String buildArticleRepresentation(Optional<String> articleTitle) {
        return articleTitle
                .map(title -> {
                    String url = null;
                    try {
                        url = WIKIPEDIA_ARTICLE_URL + URLEncoder.encode(title.replace(' ', '_'),
                                StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                    return String.format("<a href=\"%s\">%s</a>", url, StringEscapeUtils.escapeHtml4(title));
                })
                .orElse("Request failed");
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
            @JsonProperty("random") List<Item> random;
        }

        @JsonProperty("batchcomplete") String batchComplete;
        @JsonProperty("continue") ContinuationToken continuationToken;
        @JsonProperty("query") Query query;
    }
}
