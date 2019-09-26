package com.planet_lia.match_generator.libs;

/**
 * An accurate sync method that adapts automatically
 * to the system it runs on to provide reliable results.
 *
 * @author originaly by kappa (On the LWJGL Forums)
 */
public class FPSLimiter {

    private long variableYieldTime = 0;
    private long lastTime = 0;

    public void sync(long fps) {
        // Nanoseconds to sleep this frame
        long sleepTime = (1000000000 / fps);

        // yieldTime + remainder micro & nano seconds if smaller than sleepTime
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000 * 1000));
        // elapsedTime the sync goes over by
        long overSleep = 0;

        try {
            while (true) {
                long t = System.nanoTime() - lastTime;

                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                } else if (t < sleepTime) {
                    // Burn the last few CPU cycles to ensure accuracy
                    Thread.yield();
                } else {
                    overSleep = t - sleepTime;
                    break; // Exit while loop
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);

            // auto tune the elapsedTime sync should yield
            if (overSleep > variableYieldTime) {
                // increase by 200 microseconds (1/5 a ms)
                variableYieldTime = Math.min(variableYieldTime + 200 * 1000, sleepTime);
            } else if (overSleep < variableYieldTime - 200 * 1000) {
                // decrease by 2 microseconds
                variableYieldTime = Math.max(variableYieldTime - 2 * 1000, 0);
            }
        }
    }
}