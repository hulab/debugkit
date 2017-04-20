package com.hulab.debugkit;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nebneb on 26/10/2016 at 11:27.
 */

public class DevToolFragment extends Fragment {

    private static final int MINIFY_WIDTH = 132;
    private int CONSOLE_HEIGHT = 110;
    private int CONSOLE_WIDTH = 250;
    private int CONSOLE_TEXT_SIZE = 12;

    private View mRootView;

    private LayoutInflater mInflater;

    private List<DebugFunction> mFunctions = new ArrayList<>();

    private TextView mConsole;
    private ScrollView mConsoleContainer;

    private View mPanel;

    private View mMinifyButton;

    private float dX;
    private float dY;

    private DevToolTheme mTheme = DevToolTheme.DARK;

    public DevToolFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mRootView = inflater.inflate(R.layout.fragment_dev_tools, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout mButtonContainer = (LinearLayout) mRootView.findViewById(R.id.button_container);

        for (int i = 0; i < mFunctions.size(); i++) {

            Button button = (Button) mInflater
                    .inflate(mTheme == DevToolTheme.DARK ? R.layout.function_button_dark : R.layout.function_button_light, mButtonContainer, false);
            final DebugFunction function = mFunctions.get(i);
            final String title = "F" + (i + 1);

            button.setText(title);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (function != null) {
                            String result = function.call();
                            if (result != null)
                                log(title + ": " + result);
                            else
                                log(title + " was called");
                        } else
                            log(title + " is undefined");
                    } catch (Exception e) {
                        log("Error: see logcat for more details");
                        e.printStackTrace();
                    }
                }
            });

            mButtonContainer.addView(button);
        }

        mConsole = (TextView) mRootView.findViewById(R.id.console);
        mConsoleContainer = (ScrollView) mRootView.findViewById(R.id.console_scroll_view);

        mMinifyButton = mRootView.findViewById(R.id.tools_minify);

        mPanel = mRootView.findViewById(R.id.tools_panel);

        mRootView.findViewById(R.id.tools_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    try {
                        getActivity().getFragmentManager()
                                .beginTransaction()
                                .remove(DevToolFragment.this)
                                .commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return DevToolFragment.this.onTouch(v, event);
            }
        });


        ViewGroup.LayoutParams layoutParams = mConsoleContainer.getLayoutParams();
        layoutParams.height = dpTopX(CONSOLE_HEIGHT);
        mConsoleContainer.setLayoutParams(layoutParams);

        layoutParams = mConsole.getLayoutParams();
        layoutParams.height = dpTopX(CONSOLE_HEIGHT);
        layoutParams.width = dpTopX(CONSOLE_WIDTH);
        mConsole.setLayoutParams(layoutParams);
        mConsole.setMinimumHeight(dpTopX(CONSOLE_HEIGHT));

        mMinifyButton.setTag(mMinifyButton.getId(), false);
        mMinifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMinify();
            }
        });
        applyTheme();

        softLog("ready.");
    }

    /**
     * Switch the tool to minify mode.
     */
    private void switchMinify() {

        RotateAnimation rotateAnimation;
        ValueAnimator heightValueAnimator;
        ValueAnimator widthValueAnimator;

        if ((boolean) mMinifyButton.getTag(mMinifyButton.getId())) {
            rotateAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            heightValueAnimator = ValueAnimator.ofInt(0, dpTopX(CONSOLE_HEIGHT));
            widthValueAnimator = ValueAnimator.ofInt(dpTopX(MINIFY_WIDTH), dpTopX(CONSOLE_WIDTH));
            mMinifyButton.setTag(mMinifyButton.getId(), false);
        } else {
            rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            heightValueAnimator = ValueAnimator.ofInt(dpTopX(CONSOLE_HEIGHT), 0);
            widthValueAnimator = ValueAnimator.ofInt(dpTopX(CONSOLE_WIDTH), dpTopX(MINIFY_WIDTH));
            mMinifyButton.setTag(mMinifyButton.getId(), true);
        }

        heightValueAnimator.setDuration(200);
        heightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mConsoleContainer.getLayoutParams().height = value.intValue();
                mConsoleContainer.requestLayout();
            }
        });
        widthValueAnimator.setDuration(200);
        widthValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mConsole.getLayoutParams().width = value.intValue();
                mConsole.requestLayout();
            }
        });

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        mMinifyButton.startAnimation(rotateAnimation);
        heightValueAnimator.setInterpolator(new AccelerateInterpolator());
        heightValueAnimator.start();
        widthValueAnimator.setInterpolator(new AccelerateInterpolator());
        widthValueAnimator.start();
    }


    private boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                v.setX(event.getRawX() + dX);
                v.setY(event.getRawY() + dY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }

        return true;
    }

    /**
     * Call this function at runtime if you want to log something in the console.
     *
     * @param string the message that will be logged to to the console.
     * <p>
     * string will be logged in the console on a new line as following:
     * <br>
     * {@code HH:mm:ss > string}
     *
     */
    public void log(final String string) {
        final StringBuilder sb = new StringBuilder(mConsole.getText());
        sb.append("\n");
        sb.append(getCurrentTime()).append(" > ");
        sb.append(string);
        write(sb.toString());
    }

    private void softLog(String string) {
        final StringBuilder sb = new StringBuilder(mConsole.getText());
        sb.append(getCurrentTime()).append(" > ");
        sb.append(string);
        write(sb.toString());
    }

    private void write(final String string) {
        mConsole.setText(string);
        mConsole.post(new Runnable() {
            @Override
            public void run() {
                mConsole.requestLayout();
                if (mConsoleContainer != null) {
                    mConsoleContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            mConsoleContainer.fullScroll(ScrollView.FOCUS_DOWN);
                            mConsoleContainer.requestLayout();
                        }
                    });
                }
            }
        });
    }


    /**
     * Add a function to the list. This will add a button as well when calling {@code build()}
     *
     * @param function must implement {@link DebugFunction}.
     */
    public void addFunction(DebugFunction function) {
        this.mFunctions.add(function);
    }

    /**
     * Set the function list. This will corresponding buttons when calling {@code build()}
     *
     * @param functions must be a List of {@link DebugFunction}.
     */
    public void setFunctionList(List<DebugFunction> functions) {
        this.mFunctions = functions;
    }

    /**
     * Set the console text size. Must be called after having called build()
     *
     * @param sp the size of the text in sp.
     */
    public void changeConsoleTextSize(final int sp) {
        mConsole.post(new Runnable() {
            @Override
            public void run() {
                mConsole.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
            }
        });
    }

    /**
     * Set the console text size. The size will be applied on build.
     *
     * @param sp the size of the text in sp.
     */
    public void setConsoleTextSize(final int sp) {
        CONSOLE_TEXT_SIZE = sp;
    }

    /**
     * Set the theme of the debug tool
     *
     * @param theme can be {@code DevToolTheme.LIGHT} or {@code DevToolTheme.DARK}.
     *              The default theme is {@code DevToolTheme.DARK}
     */
    public void setTheme(DevToolTheme theme) {
        this.mTheme = theme;
    }


    /**
     * This method will be called on build. You can call this method if you want to change the
     * theme of the console theme at runtime.
     */
    public void applyTheme() {
        switch (mTheme) {
            case LIGHT:
                mConsole.setBackgroundColor(getColor(R.color.debug_kit_primary_light));
                mConsole.setTextColor(getColor(R.color.debug_kit_background_black_light));
                break;
            default:
                mConsole.setBackgroundColor(getColor(R.color.debug_kit_background_black));
                mConsole.setTextColor(getColor(R.color.debug_kit_primary));

        }

        mConsole.setTextSize(TypedValue.COMPLEX_UNIT_SP, CONSOLE_TEXT_SIZE);
    }

    private int getColor(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(resId, null);
        } else {
            return getResources().getColor(resId);
        }
    }

    private int dpTopX(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    private String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    /**
     * Enum, theme choices for the debug tool.
     */
    public enum DevToolTheme {
        DARK,
        LIGHT
    }

    /**
     * Set the console height.
     *
     * @param consoleHeight represents the console height in dp.
     */
    public void setConsoleHeight(int consoleHeight) {
        this.CONSOLE_HEIGHT = consoleHeight;
    }

    /**
     * Set the console width.
     *
     * @param consoleWidth represents the console width in dp.
     */
    public void setConsoleWidth(int consoleWidth) {
        this.CONSOLE_WIDTH = consoleWidth;
    }
}
