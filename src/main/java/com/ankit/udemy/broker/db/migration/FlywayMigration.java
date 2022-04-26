package com.ankit.udemy.broker.db.migration;

import com.ankit.udemy.broker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.java.Log;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {
  private static final Logger LOG= LoggerFactory.getLogger(FlywayMigration.class);
  public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {

    return vertx.<Void>executeBlocking(promise -> {
      execute(dbConfig);
      promise.complete();
    })
      .onFailure(err->LOG.error("failed to migrate db schema with error: ", err));
  }

  private static void execute(DbConfig dbConfig) {
    final String jdbcUrl=String.format("jdbc:postgresql://%s:%d/%s",
      dbConfig.getHost(),
      dbConfig.getPort(),
      dbConfig.getDatabase());
    LOG.debug("Migrating DB schema using jdbc url: {}",jdbcUrl);

    var flyway= Flyway.configure()
       .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
       .schemas("broker")
       .defaultSchema("broker")
       .load();

    Optional<MigrationInfo> current = Optional.ofNullable(flyway.info().current());
    current.ifPresent(info->LOG.info("db schema is at version: {}",info.getVersion()));

    var pendingMigration=flyway.info().pending();
    LOG.debug("Pending Migration are: {}",printMigration(pendingMigration));

    flyway.migrate();
  }

  private static String printMigration(MigrationInfo[] pending) {
    if(Objects.isNull(pending)){
      return "[]";
    }
    return Arrays.stream(pending)
      .map(each-> each.getVersion() + " - " + each.getDescription())
      .collect(Collectors.joining(",","[","]"));
  }
}
