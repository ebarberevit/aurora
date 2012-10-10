package com.twitter.mesos.scheduler.storage;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.base.Preconditions;

/**
 * A lock manager that wraps a ReadWriteLock and detects ill-fated attempts to upgrade
 * a read-locked thread to a write-locked thread, which would otherwise deadlock.
 */
public class LockManager {
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  enum LockMode {
    NONE,
    READ,
    WRITE
  }

  private LockMode initialLockMode = LockMode.NONE;
  private int lockCount = 0;

  private void lockAcquired(LockMode mode) {
    if (initialLockMode == LockMode.NONE) {
      initialLockMode = mode;
    }
    if (initialLockMode == mode) {
      lockCount++;
    }
  }

  private void lockReleased(LockMode mode) {
    if (initialLockMode == mode) {
      lockCount--;
      if (lockCount == 0) {
        initialLockMode = LockMode.NONE;
      }
    }
  }

  /**
   * Blocks until this thread has acquired a read lock.
   */
  public void readLock() {
    lock.readLock().lock();
    lockAcquired(LockMode.READ);
  }

  /**
   * Releases this thread's read lock.
   */
  public void readUnlock() {
    lock.readLock().unlock();
    lockReleased(LockMode.READ);
  }

  /**
   * Blocks until this thread has acquired a write lock.
   */
  public void writeLock() {
    Preconditions.checkState(initialLockMode != LockMode.READ,
        "A read transaction may not be upgraded to a write transaction.");

    lock.writeLock().lock();
    lockAcquired(LockMode.WRITE);
  }

  /**
   * Releases this thread's write lock.
   */
  public void writeUnlock() {
    lock.writeLock().unlock();
    lockReleased(LockMode.WRITE);
  }
}
