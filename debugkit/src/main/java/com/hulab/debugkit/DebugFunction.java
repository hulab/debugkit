package com.hulab.debugkit;

import java.util.concurrent.Callable;

/**
 * Created by Nebneb on 22/03/2017 at 18:18.
 */

/**
 * Use this class to add a function to the debug tool. The return type of {@code call()} method will
 * be logged to the console.
 *
 * @see Callable
 */
public abstract class DebugFunction implements Callable<String> {

    private DevToolFragment mDevToolFragment;

    /**
     * The method that will be executed when matching button is clicked.
     *
     * @return The string message that will be logged to the console.
     * @throws Exception
     */
    @Override
    public abstract String call() throws Exception;

    /**
     * Calling this method will log a message in the console.
     *
     * @param string the message that will be logged to to the console.
     * <p>
     * {@param string} will be logged in the console on a new line as following:
     * <br>
     * {@code HH:mm:ss > string}
     *
     */
    protected void log(String string) {
        if (mDevToolFragment != null)
            mDevToolFragment.log(string);
    }

    /*package*/ void setDevToolFragment(DevToolFragment devToolFragment) {
        this.mDevToolFragment = devToolFragment;
    }
}
