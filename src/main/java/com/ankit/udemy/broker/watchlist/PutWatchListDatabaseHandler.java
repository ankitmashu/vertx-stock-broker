package com.ankit.udemy.broker.watchlist;

import com.ankit.udemy.broker.db.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PutWatchListDatabaseHandler implements Handler<RoutingContext> {
 private static final Logger LOG= LoggerFactory.getLogger(PutWatchListDatabaseHandler.class);
  private final Pool db;

  public PutWatchListDatabaseHandler(Pool db) {
    this.db=db;
  }

  @Override
  public void handle(RoutingContext context) {
  var accountId=  WatchListRestApi.getAccountId(context);
    var json = context.getBodyAsJson();
    var watchList= json.mapTo(WatchList.class);

     var parameterBatch= watchList.getAssets().stream()
        .map(asset -> {
          final Map<String, Object> parameters= new HashMap<>();
          parameters.put("account_id",accountId);
          parameters.put("asset",asset.getName());
          return  parameters;
        }).collect(Collectors.toList());
//only adding is possible -> Enteries for watch list never removed
      SqlTemplate.forUpdate(db,
          "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset})" +
          " ON CONFLICT (account_id, asset) DO NOTHING")
        .executeBatch(parameterBatch)
        .onFailure(DbResponse.errorHandler(context,"Failed to insert into watchlist"))
        .onSuccess(result->
            context.response()
              .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
              .end()
        );
  }
}