package com.ankit.udemy.broker.watchlist;

import com.ankit.udemy.broker.AbstractRestApiTest;
import com.ankit.udemy.broker.assets.Asset;
import io.vertx.core.Future;
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

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi extends AbstractRestApiTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);
  @Test
  void add_and_returns_watchList_for_account(Vertx vertx, VertxTestContext context) throws Throwable {
    WebClient client = webClient(vertx);
    var accountId= UUID.randomUUID();
    client.put("/account/watchlist/" + accountId.toString())
      .sendJsonObject(body())
      .onComplete(context.succeeding(response->{
        JsonObject json=response.bodyAsJsonObject();
        LOG.info("Response: {}",json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}",json.encode());
        assertEquals(200,response.statusCode());
        context.completeNow();
      })).compose(next->{
        client.get("/account/watchlist/" + accountId.toString())
          .send()
          .onComplete(context.succeeding(response->{
            var json=response.bodyAsJsonObject();
            LOG.info("Response PUT: {}",json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}",json.encode());
            assertEquals(200,response.statusCode());
            context.completeNow();
          }));
        return Future.succeededFuture();
      })
    ;
  }
@Test
void adds_and_deletes_watchlist_for_account(Vertx vertx, VertxTestContext context) {
  WebClient client = webClient(vertx);
  var accountId = UUID.randomUUID();

  client.put("/account/watchlist/" + accountId.toString())
    .sendJsonObject(body())
    .onComplete(context.succeeding(response -> {
      JsonObject json = response.bodyAsJsonObject();
      LOG.info("Response: {}", json);
      assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
      assertEquals(200, response.statusCode());
      context.completeNow();
    }))
    .compose(next -> {
      client.delete("/account/watchlist/" + accountId.toString())
        .send()
        .onComplete(context.succeeding(response -> {
          var json = response.bodyAsJsonObject();
          LOG.info("Response DELETE: {}", json);
          assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
          assertEquals(200, response.statusCode());
          context.completeNow();
        }));
      return Future.succeededFuture();
    });
}
  private JsonObject body() {
    return new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA")))
      .toJsonObject();
  }

  private WebClient webClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
  }

}
