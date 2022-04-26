package com.ankit.udemy.broker;

import com.ankit.udemy.broker.MainVerticle;
import com.ankit.udemy.broker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public  abstract class AbstractRestApiTest {
  private static final Logger LOG=LoggerFactory.getLogger(AbstractRestApiTest.class);
  protected static final Integer TEST_SERVER_PORT = 9000;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext context){
    System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf((TEST_SERVER_PORT)));
    System.setProperty(ConfigLoader.DB_HOST,"localhost");
    System.setProperty(ConfigLoader.DB_PORT,"5432");
    System.setProperty(ConfigLoader.DB_USER,"postgres");
    System.setProperty(ConfigLoader.DB_DATABASE,"vertx-stock-broker");
    System.setProperty(ConfigLoader.DB_PASSWORD,"secret");
    LOG.warn("!!! Test are using local database !!!");
    vertx.deployVerticle(new MainVerticle(),context.succeeding((id-> context.completeNow())));
  }
}
