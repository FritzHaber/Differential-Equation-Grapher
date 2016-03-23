package util;

import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class Util {
  /**
   * Main parallel executor for the program. Maintains one thread for each available processor for
   * maximal parallelism. Provides as many convenience executor functionalities as possible. Should
   * ONLY be used for tasks doing continuous work, NOT for those waiting for something else to
   * happen. A task which waits on this executor will block others from being processed.
   * 
   */
  public static final ListeningScheduledExecutorService compute = MoreExecutors.listeningDecorator(
      Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()));

  /**
   * Executor for tasks which block, wait, or are interdependent. All submitted tasks will be
   * executed concurrently, but with increasing overhead with greater numbers of tasks.
   */
  public static final ListeningExecutorService exec =
      MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
}
