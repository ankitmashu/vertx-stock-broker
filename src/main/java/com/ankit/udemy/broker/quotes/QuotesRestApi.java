package com.ankit.udemy.broker.quotes;

import com.ankit.udemy.broker.assets.AssestsRestApi;
import com.ankit.udemy.broker.assets.Asset;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {
  public static void attach(Router parents, Pool db)
  {
    final Map<String,Quote> cachedQuotes= new HashMap<>();
    AssestsRestApi.ASSETS.forEach(symbol->
      cachedQuotes.put(symbol,initRandomQuote(symbol))
    );

    parents.get("/quotes/:asset").handler(new GetQuoteHandler(cachedQuotes));
    parents.get("/pg/quotes/:asset").handler(new GetQuoteFromDatabaseHandler(db));

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
