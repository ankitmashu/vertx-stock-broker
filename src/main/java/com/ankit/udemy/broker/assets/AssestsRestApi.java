package com.ankit.udemy.broker.assets;

import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;

import java.util.Arrays;
import java.util.List;

public class AssestsRestApi {
 public static final List<String> ASSETS= Arrays.asList("AAPL","AMZN","FB","GOOG","NFLX","MSFT","TLSA");
  public static void attach(Router parents, PgPool db)
  {
    parents.get("/assets").handler(new GetAssetHandler());
  }
}
