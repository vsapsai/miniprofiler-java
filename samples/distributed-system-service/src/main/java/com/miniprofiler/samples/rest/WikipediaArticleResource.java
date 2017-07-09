package com.miniprofiler.samples.rest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.miniprofiler.samples.WikipediaClient;

@Path("/wikipedia/article")
public class WikipediaArticleResource {
    private static String WIKIPEDIA_ARTICLE_URL = "https://en.wikipedia.org/wiki/";

    private final WikipediaClient wikipediaClient = new WikipediaClient();

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/random")
    public Response getRandomArticle() throws IOException {
        String randomArticleTitle = wikipediaClient.getRandomWikipediaArticleName();
        if (randomArticleTitle == null) {
            return Response
                    .status(Response.Status.BAD_GATEWAY)
                    .entity("Request failed")
                    .build();
        }
        return Response
                .status(Response.Status.OK)
                .entity(buildArticleRepresentation(randomArticleTitle))
                .build();
    }

    private WikipediaArticle buildArticleRepresentation(String articleTitle) throws IOException {
        WikipediaArticle article = new WikipediaArticle();
        article.title = articleTitle;
        article.url = WIKIPEDIA_ARTICLE_URL + URLEncoder.encode(articleTitle.replace(' ', '_'),
                StandardCharsets.UTF_8.name());
        return article;
    }
}
