package com.ankit.udemy.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssestsRestApi {
 public static final List<String> ASSETS= Arrays.asList("AAPL","AMZN","FB","GOOG","NFLX","MSFT","TLSA");
  public static void attach(Router parents)
  {
    parents.get("/assets").handler(new GetAssetHandler());
  }
}
