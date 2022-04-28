package com.ankit.udemy.broker.watchlist;

import com.ankit.udemy.broker.db.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DeleteWatchListDatabaseHandler implements Handler<RoutingContext> {
  private final Pool db;
private static final Logger LOG= LoggerFactory.getLogger(DeleteWatchListDatabaseHandler.class);
  public DeleteWatchListDatabaseHandler(Pool db) {
    this.db=db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId= WatchListRestApi.getAccountId(context);

    SqlTemplate.forUpdate(db,
      "DELETE FROM broker.watchlist where account_id=#{account_id}")
      .execute(Collections.singletonMap("account_id",accountId))
      .onFailure(DbResponse.errorHandler(context,"Failed to delete watchlist for accountId: " + accountId))
      .onSuccess(result->{
        LOG.debug("Deleted {} rows for accountId {}", result.rowCount(), accountId);
        context.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
    ;
  }
}
