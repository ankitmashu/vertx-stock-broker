package com.ankit.udemy.broker;

import com.ankit.udemy.broker.assets.AssestsRestApi;
import com.ankit.udemy.broker.config.BrokerConfig;
import com.ankit.udemy.broker.config.ConfigLoader;
import com.ankit.udemy.broker.quotes.QuotesRestApi;
import com.ankit.udemy.broker.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
        .onFailure(startPromise::fail)
          .onSuccess(configuration->{
            LOG.info("Retrieved Configuration: {}",configuration);
            startHttpServerAndAttachRoutes(startPromise,configuration);
          });
    }
    private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, final BrokerConfig configuration) {
      final Router restApi= Router.router(vertx);
      restApi.route()
        .handler(BodyHandler.create()
          //// .setBodyLimit(1024)
          //.setHandleFileUploads(true)
        )
        .failureHandler(handleFailure());
      AssestsRestApi.attach(restApi);
      QuotesRestApi.attach(restApi);
      WatchListRestApi.attach(restApi);

      vertx.createHttpServer()
        .requestHandler(restApi)
        .exceptionHandler(error->LOG.error("HTTPS SERVER ERROR",error))
        .listen(configuration.getServerPort(), http -> {
          if (http.succeeded()) {
            startPromise.complete();
            LOG.info("HTTP server started on port {}",configuration.getServerPort());
          } else {
            startPromise.fail(http.cause());
          }
        });
    }

    private Handler<RoutingContext> handleFailure() {
      return errorContext -> {
        if (errorContext.response().ended()) {
          //Ignore Completed Response
          return;
        }
        LOG.error("Route Error:", errorContext.failure());
        errorContext.response()
          .setStatusCode(500)
          .end(new JsonObject().put("message", "something went wrong: (").toBuffer());
      };
    }
  }

