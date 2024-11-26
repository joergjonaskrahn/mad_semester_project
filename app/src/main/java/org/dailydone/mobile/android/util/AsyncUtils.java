package org.dailydone.mobile.android.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncUtils {
    // This method reduces Boilerplate code for tasks which have to be executed
    // asynchronously.
    public static void executeAsync(Runnable task) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                task.run();
            } finally {
                executorService.shutdown();
            }
        });
    }
}