package com.twitter.mesos.scheduler.storage.mem;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.Atomics;

import com.twitter.mesos.scheduler.storage.SchedulerStore;

/**
 * An in-memory scheduler store.
 */
public class MemSchedulerStore implements SchedulerStore.Mutable {
  private final AtomicReference<String> frameworkId = Atomics.newReference();

  @Override
  public void saveFrameworkId(String newFrameworkId) {
    frameworkId.set(newFrameworkId);
  }

  @Nullable
  @Override
  public String fetchFrameworkId() {
    return frameworkId.get();
  }
}
