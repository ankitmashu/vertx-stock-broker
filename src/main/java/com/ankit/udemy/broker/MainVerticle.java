package com.ankit.udemy.broker;

import com.ankit.udemy.broker.assets.AssestsRestApi;
import com.ankit.udemy.broker.config.ConfigLoader;
import com.ankit.udemy.broker.db.migration.FlywayMigration;
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


  public static void main(String[] args) {
    //System.setProperty(ConfigLoader.SERVER_PORT,"9000");
    Vertx vertx=Vertx.vertx();
    vertx.exceptionHandler(error->
     LOG.error("Unhandled: ", error)
    );
    vertx.deployVerticle(new MainVerticle())
      .onFailure(err->LOG.error("Failed to deploy: ",err))
      .onSuccess(id->{
        LOG.info("Deployed {} with id {}",RestApiVerticle.class.getSimpleName(),id);
        });
      }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(VersionInfoVerticle.class.getName())
        .onFailure(startPromise::fail)
      .onSuccess(id ->LOG.info("Deployed {} with id {}", VersionInfoVerticle.class.getSimpleName(), id))
      .compose(next-> migrateDatabase())
      .onFailure(startPromise::fail)
      .onSuccess(id-> LOG.info("Migrated db schema to latest version!"))
          .compose(next-> deployRestApiVerticle(startPromise)
          );
  }

  private Future<Void> migrateDatabase() {
   return ConfigLoader.load(vertx)
      .compose(config->{
        return FlywayMigration.migrate(vertx,config.getDbConfig());
      });
  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
        new DeploymentOptions().setInstances(processor()))
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        LOG.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
        startPromise.complete();
      });
  }

  private int processor() {
    return Math.max(1,Runtime.getRuntime().availableProcessors());
  }

}
