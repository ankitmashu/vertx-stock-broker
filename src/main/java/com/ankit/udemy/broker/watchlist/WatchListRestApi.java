package com.ankit.udemy.broker.watchlist;

import com.ankit.udemy.broker.MainVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class WatchListRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static void attach(final Router parent, Pool db){
    final HashMap<UUID,WatchList> watchListPerAccount= new HashMap<UUID, WatchList>();
    final String path="/account/watchlist/:accountId";
    parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));
    parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));
    parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));

    final String pgPath="/pg/account/watchlist/:accountId";
    parent.get(pgPath).handler(new GetWatchListFromDatabaseHandler(db));
  }

   static String getAccountId(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(),accountId);
    return accountId;
  }

}
