package com.ankit.udemy.broker.quotes;

import com.ankit.udemy.broker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void returns_quote_for_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/quotes/AMZN")
      .send()
      .onComplete(context.succeeding(response->{
        JsonObject json=response.bodyAsJsonObject();
        LOG.info("Response: {}",json);
        assertEquals("{\"name\":\"AMZN\"}",json.getJsonObject("asset").encode());
        assertEquals(200,response.statusCode());
        context.completeNow();
      }));
  }

  @Test
  void returns_not_found_for_unkown_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(context.succeeding(response->{
        JsonObject json=response.bodyAsJsonObject();
        LOG.info("Response: {}",json);
        assertEquals(404,response.statusCode());
        assertEquals("{\"message\":\"quote for asset UNKNOWNnot available\",\"path\":\"/quotes/UNKNOWN\"}",json.encode());

        context.completeNow();
      }));
  }
}
