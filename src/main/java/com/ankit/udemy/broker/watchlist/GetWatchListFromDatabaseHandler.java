package com.ankit.udemy.broker.watchlist;

import com.ankit.udemy.broker.db.DbResponse;
import com.ankit.udemy.broker.quotes.GetQuoteFromDatabaseHandler;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetWatchListFromDatabaseHandler implements Handler<RoutingContext> {
  private final Pool db;
  private static final Logger LOG = LoggerFactory.getLogger(GetWatchListFromDatabaseHandler.class);


  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);

    SqlTemplate.forQuery(db,
      "SELECT w.asset FROM broker.watchlist w where w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id",accountId))
      .onFailure(DbResponse.errorHandler(context,"Failed to fetch watchlist for account id " + accountId))
      .onSuccess(assets->{
       if (!assets.iterator().hasNext()){
         DbResponse.notFound(context,"watchlist for accountid "+ accountId + " is not available!");
         return;
       }
       var response = new JsonArray();
       assets.forEach(response::add);
        LOG.info("Path {} responds with {}", context.normalizedPath(),response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });

  }

  public GetWatchListFromDatabaseHandler(Pool db) {
    this.db=db;
  }
}
