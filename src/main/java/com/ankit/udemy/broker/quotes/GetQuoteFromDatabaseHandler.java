package com.ankit.udemy.broker.quotes;

import com.ankit.udemy.broker.db.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetQuoteFromDatabaseHandler implements Handler<RoutingContext> {
  private final Pool db;
  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteFromDatabaseHandler.class);
  public GetQuoteFromDatabaseHandler(Pool db) {
    this.db=db;
  }

  @Override
  public void handle(RoutingContext context) {
    final String assetParam=context.pathParam("asset");
    LOG.debug("Asset parameter:{}", assetParam);

    SqlTemplate.forQuery(db,
      "select q.asset,q.bid,q.ask,q.last_price,q.volume from broker.quotes q where asset=#{asset}")
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset",assetParam))
      .onFailure(DbResponse.errorHandler(context,"Failed to get Quotes for asset "+ assetParam + "from db!!!"))
      .onSuccess(quotes->{
        if(!quotes.iterator().hasNext())
        {
          //NO entry
          DbResponse.notFound(context,"quote for asset "+ assetParam + " not available");
          return;
        }
        var response= quotes.iterator().next().toJsonObject();
        LOG.info("Path {} responds with {}", context.normalizedPath(),response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
