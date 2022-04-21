package com.ankit.udemy.broker.assets;

import com.ankit.udemy.broker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
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
public class TestAssetRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(TestAssetRestApi.class);
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void returns_all_assets(Vertx vertx, VertxTestContext context) throws Throwable {
    WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/assets")
      .send()
      .onComplete(context.succeeding(response->{
        JsonArray json=response.bodyAsJsonArray();
        LOG.info("Response: {}",json);
        assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"FB\"},{\"name\":\"GOOG\"},{\"name\":\"NFLX\"},{\"name\":\"MSFT\"},{\"name\":\"TLSA\"}]",json.encode());
        assertEquals(200,response.statusCode());
        context.completeNow();
      }));
  }
}
