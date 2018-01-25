package com.hulab.debugkit;

import android.content.Context;
import android.content.SharedPreferences;

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
    public String title = null;

    public DebugFunction() {
    }

    /**
     * By default, the buttons of the tool are F1, F2... You can give a title to this button
     * using this constructor.
     *
     * @param title the title of the function you want the button to display
     */
    public DebugFunction(String title) {
        this.title = title;
    }

    /**
     * The method that will be executed when matching button is clicked.
     *
     * @return The string message that will be logged to the console.
     *          if null is returned, no log message will be displayed.
     * @throws Exception
     */
    @Override
    public abstract String call() throws Exception;

    /**
     * Calling this method will log a message in the console.
     *
     * @param string the message that will be logged to to the console.
     * <p>
     * string will be logged in the console on a new line as following:
     * <br>
     * {@code HH:mm:ss > string}
     *
     */
    protected void log(String string) {
        if (mDevToolFragment != null)
            mDevToolFragment.log(string);
    }

    /**
     * Calling this method will clear the console.
     */
    protected void clear() {
        if (mDevToolFragment != null)
            mDevToolFragment.clear();
    }

    /*package*/ void setDevToolFragment(DevToolFragment devToolFragment) {
        this.mDevToolFragment = devToolFragment;
    }

    public static class Clear extends DebugFunction {
        @Override
        public String call() throws Exception {
            clear();
            return null;
        }
    }

    public static class DumpSharedPreferences extends DebugFunction {
        private String FILE_NAME;
        private int mode = Context.MODE_PRIVATE;
        private Context mContext;

        private DumpSharedPreferences() {
        }

        public DumpSharedPreferences(String title, Context context, String fileName, int mode) {
            super(title);
            this.FILE_NAME = fileName;
            this.mode = mode;
            this.mContext = context;
        }

        public DumpSharedPreferences(String title, Context context, String fileName) {
            super(title);
            this.FILE_NAME = fileName;
            this.mContext = context;
        }

        public DumpSharedPreferences(Context context, String fileName, int mode) {
            this.FILE_NAME = fileName;
            this.mode = mode;
            this.mContext = context;
        }

        public DumpSharedPreferences(Context context, String fileName) {
            this.FILE_NAME = fileName;
            this.mContext = context;
        }

        @Override
        public String call() throws Exception {
            return dumpSharedPreferences(mContext);
        }

        private String dumpSharedPreferences(Context context) {
            SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, mode);
            java.util.Map<String, ?> keys = preferences.getAll();

            StringBuilder sb = new StringBuilder();

            for (java.util.Map.Entry<String, ?> entry : keys.entrySet())
                sb.append(entry).append(",\n");

            return sb.toString();
        }

    }
}
