package com.ankit.udemy.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsFromDatabaseHandler implements Handler<RoutingContext> {
  private final PgPool db;
  private static final Logger LOG= LoggerFactory.getLogger(GetAssetsFromDatabaseHandler.class);


  public GetAssetsFromDatabaseHandler(PgPool db) {
    this.db=db;
  }

  @Override
  public void handle(RoutingContext context) {
    db.query("SELECT a.value FROM broker.assets a")
      .execute()
      .onFailure(error-> {
        LOG.error("failure: ",error);
        context.response()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(new JsonObject()
            .put("message", "Failed to get assets from db!")
            .put("path", context.normalizedPath())
            .toBuffer()
          );
      })
      .onSuccess(result-> {
        var response = new JsonArray();
        result.forEach(row->{
          response.add(row.getValue("value"));
        });
        LOG.info("Path {} responds with {}", context.normalizedPath(),response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      })
    ;
  }
}