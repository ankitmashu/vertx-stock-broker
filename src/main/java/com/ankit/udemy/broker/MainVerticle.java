package com.ankit.udemy.broker;

import com.ankit.udemy.broker.assets.AssestsRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
 private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static void main(String[] args) {
    Vertx vertx=Vertx.vertx();
    vertx.exceptionHandler(error->
     LOG.error("Unhandled: ", error)
    );
    vertx.deployVerticle(new MainVerticle(), ar->{
      if(ar.failed()){
        LOG.error("Failed to depply: ",ar.cause());
        return;
      }
      LOG.info("Deployed {}", MainVerticle.class.getName());
  });
  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    final Router restApi= Router.router(vertx);
    restApi.route().failureHandler(errorContext->{
      if(errorContext.response().ended())
      {
        //Ignore Completed Response
        return;
      }
      LOG.error("Route Error:", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message","something went wrong: (").toBuffer());
    });
    AssestsRestApi.attach(restApi);
    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error->LOG.error("HTTPS SERVER ERROR",error))
      .listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
