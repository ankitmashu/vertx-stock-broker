package com.ankit.udemy.broker.quotes;
import com.ankit.udemy.broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.Value;
import lombok.Builder;

import java.math.BigDecimal;

@Value
@Builder
public class Quote {
  Asset asset;
  BigDecimal bid;
  BigDecimal ask;
  BigDecimal lastPrice;
  BigDecimal volume;

  public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }

}
