package com.hulab.debugkit;

import android.app.Activity;
import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nebneb on 21/03/2017 at 17:14.
 */

public class DevTool {

    public final static boolean devToolsEnabled = true;

    /**
     * Builder class for {@link DevToolFragment}
     */
    public static class Builder {

        private List<DebugFunction> mFunctions = new ArrayList<>();

        private DevToolFragment fragment;
        private Activity activity;

        private DevToolFragment.DevToolTheme mTheme = DevToolFragment.DevToolTheme.DARK;
        private Integer mTextSize = null;

        /**
         * Constructor
         *
         * @param activity the activity the DevTool will be linked to.
         */
        public Builder(Activity activity) {
            fragment = new DevToolFragment();
            this.activity = activity;
        }

        /**
         * Add function to the function list. This will generate a button on the tool's panel.
         *
         * @param function will be called on the matching button click, and the return String
         *                 of the function will be logged in the console as soon as the function
         *                 ended.
         * @return this to allow chaining.
         */
        public Builder addFunction(DebugFunction function) {
            if (function != null) {
                function.setDevToolFragment(fragment);
                this.mFunctions.add(function);
            }
            return this;
        }

        /**
         * Set the theme of the debug tool. The theme will be applied on build() call.
         *
         * @param theme can be {@code DevToolTheme.LIGHT} or {@code DevToolTheme.DARK}.
         *              The default theme is {@code DevToolTheme.DARK}
         * @return this to allow chaining.
         */
        public Builder setTheme(DevToolFragment.DevToolTheme theme) {
            this.mTheme = theme;
            return this;
        }

        /**
         * Set te console text size. By default the text size is 12sp.
         *
         * @param sp the console text size in sp.
         *
         * @return this to allow chaining.
         */
        public Builder setTextSize(int sp){
            this.mTextSize = sp;
            return this;
        }

        /**
         * Build the tool and show it.
         *
         * @return this to allow chaining.
         */
        public Builder build() {
            if (devToolsEnabled) {
                if (mFunctions != null && mFunctions.size() > 0)
                    fragment.setFunctionList(mFunctions);

                if (mTextSize != null)
                    fragment.setConsoleTextSize(mTextSize);

                try {
                    FragmentManager fragmentManager = activity.getFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(android.R.id.content, fragment)
                            .commit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                fragment.setTheme(mTheme);
            }
            return this;
        }

        /**
         * Get the {@link DevToolFragment} instance created by the builder.
         */
        public DevToolFragment getTool() {
            return fragment;
        }
    }

}
