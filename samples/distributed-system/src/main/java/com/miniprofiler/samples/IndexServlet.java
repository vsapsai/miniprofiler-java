package com.miniprofiler.samples;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serverUrl = getServerUrl(request);
        Optional<WikipediaArticle> randomArticle = getRandomWikipediaArticle(serverUrl);
        String articleUrl = randomArticle.map(this::buildArticleUrl).orElse("Request failed");
        request.setAttribute("article", articleUrl);
        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
    }

    private Optional<WikipediaArticle> getRandomWikipediaArticle(String serverUrl) throws IOException {
        MiniProfiler profiler = MiniProfiler.getCurrent();
        // Get response from distributed-system-service.
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(serverUrl + "/distributed-system-service/rest/wikipedia/article/random");
        String responseContent = "";
        try (Closeable step = profiler.step("distributed-system-service");
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
        WikipediaArticle article = mapper.readValue(responseContent, WikipediaArticle.class);
        return Optional.of(article);
    }

    private String getServerUrl(HttpServletRequest request) {
        StringBuffer url = new StringBuffer()
            .append(request.getScheme())
            .append("://")
            .append(request.getServerName())
            .append(":")
            .append(request.getServerPort());
        return url.toString();
    }

    private String buildArticleUrl(WikipediaArticle article) {
        return String.format("<a href=\"%s\">%s</a>", article.url, StringEscapeUtils.escapeHtml4(article.title));
    }

    private static class WikipediaArticle {
        public String title;
        public String url;
    }
}
