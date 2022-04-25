package com.ankit.udemy.broker;

import com.ankit.udemy.broker.assets.AbstractRestApiTest;
import com.ankit.udemy.broker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle extends AbstractRestApiTest {

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
