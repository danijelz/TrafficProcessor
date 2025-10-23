package com.example.traficprocessor.app;

import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

import com.example.traficprocessor.core.domain.LocalTraficEventCache;
import com.example.traficprocessor.core.domain.utils.Values;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.time.Duration;
import org.springframework.cache.caffeine.CaffeineCacheManager;

class CaffeineLocalTraficEventCache implements LocalTraficEventCache {
  private final Cache<String, String> caffeineCache;

  public CaffeineLocalTraficEventCache(
      CaffeineCacheManager caffeineCacheManager, int expirationInMinutes) {
    this.caffeineCache =
        Caffeine.newBuilder()
            .maximumSize(1_000_000)
            .expireAfter(new TraficEventExpiry(expirationInMinutes))
            .executor(newVirtualThreadPerTaskExecutor())
            .recordStats()
            .<String, String>build();
    var customCache = Values.<Cache<Object, Object>>cast(caffeineCache);
    caffeineCacheManager.registerCustomCache("TraficEvents", customCache);
  }

  @Override
  public boolean register(String trafficEventId) {
    return caffeineCache.asMap().put(trafficEventId, trafficEventId) == null;
  }

  private static final class TraficEventExpiry implements Expiry<String, String> {
    private final Duration expiration;

    public TraficEventExpiry(int expirationInSeconds) {
      this.expiration = Duration.ofSeconds(expirationInSeconds);
    }

    @Override
    public long expireAfterCreate(String key, String value, long currentTime) {
      return expiration.toNanos();
    }

    @Override
    public long expireAfterUpdate(
        String key, String value, long currentTime, long currentDuration) {
      return expiration.toNanos();
    }

    @Override
    @CanIgnoreReturnValue
    public long expireAfterRead(String key, String value, long currentTime, long currentDuration) {
      return currentDuration;
    }
  }
}
