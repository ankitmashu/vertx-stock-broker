package com.ankit.udemy.broker.assets;

import com.ankit.udemy.broker.MainVerticle;
import com.ankit.udemy.broker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;

public  abstract class AbstractRestApiTest {
  protected static final Integer TEST_SERVER_PORT = 9000;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext context){
    System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf((TEST_SERVER_PORT)));
    vertx.deployVerticle(new MainVerticle(),context.succeeding((id-> context.completeNow())));
  }
}
