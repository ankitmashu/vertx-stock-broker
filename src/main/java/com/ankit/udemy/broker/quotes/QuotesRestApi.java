package com.ankit.udemy.broker.quotes;

import com.ankit.udemy.broker.MainVerticle;
import com.ankit.udemy.broker.assets.AssestsRestApi;
import com.ankit.udemy.broker.assets.Asset;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void attach(Router parents)
  {
    final Map<String,Quote> cachedQuotes= new HashMap<>();
    AssestsRestApi.ASSETS.forEach(symbol->
      cachedQuotes.put(symbol,initRandomQuote(symbol))
    );
    parents.get("/quotes/:asset").handler(context->{
      final String assetParam=context.pathParam("asset");
      LOG.debug("Asset paramter:{}", assetParam);

      var maybeQuote= Optional.ofNullable(cachedQuotes.get(assetParam));
      if(maybeQuote.isEmpty()) {
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "quote for asset " + assetParam + "not available" )
            .put("path", context.normalizedPath())
            .toBuffer()
          );
        return;
      }
      final JsonObject response= maybeQuote.get().toJsonObject();
        LOG.info("Path {} responds with {}", context.normalizedPath(),response.encode());
      context.response().end(response.toBuffer());
    });
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
      .asset(new Asset(assetParam))
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1,100));
  }
}
