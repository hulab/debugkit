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

    protected Context getContext() {
        return mDevToolFragment.getActivity();
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
        public Clear(String title) {
            super(title);
        }

        @Override
        public String call() throws Exception {
            clear();
            return null;
        }
    }

    /**
     * This is a sample function to dump shared preferences
     */
    public static class DumpSharedPreferences extends DebugFunction {
        private String FILE_NAME;
        private int mode = Context.MODE_PRIVATE;

        private DumpSharedPreferences() {
        }

        /**
         * Constructor
         *
         * @param title the title of the function
         * @param fileName the name of your shared preference file
         * @param mode the file creation mode. By default {@link android.content.Context#MODE_PRIVATE Context.MODE_PRIVATE}
         */
        public DumpSharedPreferences(String title, String fileName, int mode) {
            super(title);
            this.FILE_NAME = fileName;
            this.mode = mode;
        }

        /**
         * Constructor
         *
         * @param title the title of the function
         * @param fileName the name of your shared preference file
         */
        public DumpSharedPreferences(String title, String fileName) {
            super(title);
            this.FILE_NAME = fileName;
        }

        /**
         * Constructor
         *
         * @param fileName the name of your shared preference file
         * @param mode the file creation mode. By default {@link android.content.Context#MODE_PRIVATE Context.MODE_PRIVATE}
         */
        public DumpSharedPreferences(String fileName, int mode) {
            this.FILE_NAME = fileName;
            this.mode = mode;
        }

        /**
         * Constructor
         *
         * @param fileName the name of your shared preference file
         */
        public DumpSharedPreferences(String fileName) {
            this.FILE_NAME = fileName;
        }

        @Override
        public String call() throws Exception {
            return dumpSharedPreferences(this.getContext());
        }

        private String dumpSharedPreferences(Context context) {
            SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, mode);
            java.util.Map<String, ?> keys = preferences.getAll();

            StringBuilder sb = new StringBuilder("\n");

            for (java.util.Map.Entry<String, ?> entry : keys.entrySet())
                sb.append(entry).append(",\n");

            return sb.toString();
        }

    }


}
