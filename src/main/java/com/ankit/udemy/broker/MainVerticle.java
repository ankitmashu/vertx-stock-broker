package com.ankit.udemy.broker;

import com.ankit.udemy.broker.assets.AssestsRestApi;
import com.ankit.udemy.broker.quotes.QuotesRestApi;
import com.ankit.udemy.broker.watchlist.WatchListRestApi;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 8888;

  public static void main(String[] args) {
    Vertx vertx=Vertx.vertx();
    vertx.exceptionHandler(error->
     LOG.error("Unhandled: ", error)
    );
    vertx.deployVerticle(new MainVerticle())
      .onFailure(err->LOG.error("Failed to depoly: ",err))
      .onSuccess(id->{
        LOG.info("Deployed {} with id {}",RestApiVerticle.class.getSimpleName(),id);
        });
      }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(RestApiVerticle.class.getName(),
        new DeploymentOptions().setInstances(processor()))
      .onFailure(startPromise::fail)
      .onSuccess(id->{
        LOG.info("Deployed {} with id {}",RestApiVerticle.class.getSimpleName(),id);
        startPromise.complete();
      });
  }

  private int processor() {
    return Math.max(1,Runtime.getRuntime().availableProcessors());
  }

}
