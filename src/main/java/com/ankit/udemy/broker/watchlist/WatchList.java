package com.ankit.udemy.broker.watchlist;

import com.ankit.udemy.broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {
   List<Asset> assets;

  JsonObject toJsonObject()
   {
     return JsonObject.mapFrom(this);
   }
}
