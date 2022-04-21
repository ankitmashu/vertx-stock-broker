package com.ankit.udemy.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssestsRestApi {
  private static final Logger LOG= LoggerFactory.getLogger(AssestsRestApi.class);
 public static final List<String> ASSETS= Arrays.asList("AAPL","AMZN","FB","GOOG","NFLX","MSFT","TLSA");
  public static void attach(Router parents)
  {
    parents.get("/assets").handler(context->{

      final JsonArray response=new JsonArray();
      ASSETS.stream().map(Asset::new).forEach(response::add);
      LOG.info("Path {} responds with {}", context.normalizedPath(),response.encode());
      context.response().end(response.toBuffer());
    });
  }
}
