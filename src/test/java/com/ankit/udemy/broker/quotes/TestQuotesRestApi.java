package com.ankit.udemy.broker.quotes;

import com.ankit.udemy.broker.AbstractRestApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi extends AbstractRestApiTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);

  @Test
  void returns_quote_for_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    WebClient client = webClient(vertx);
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

  private WebClient webClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
  }

  @Test
  void returns_not_found_for_unkown_asset(Vertx vertx, VertxTestContext context) throws Throwable {
    WebClient client = webClient(vertx);
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
