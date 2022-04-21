package com.ankit.udemy.broker.watchlist;

import com.ankit.udemy.broker.MainVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  private final HashMap<UUID,WatchList> watchListPerAccount;

  public DeleteWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    String acountId=WatchListRestApi.getAccountId(context);
    WatchList deleted = watchListPerAccount.remove(UUID.fromString(acountId));
    LOG.info("Deleted: {}, Remaining: {}",deleted,watchListPerAccount.values());
    context.response()
      .end(deleted.toJsonObject().toBuffer());

  }
}
