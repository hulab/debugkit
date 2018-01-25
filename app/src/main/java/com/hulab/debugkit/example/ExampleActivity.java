package com.hulab.debugkit.example;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hulab.debugkit.DebugFunction;
import com.hulab.debugkit.DevTool;
import com.hulab.debugkit.DevToolFragment;


public class ExampleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int mTextSize = 12;
    private SeekBar mSeekbar;
    private DevToolFragment.DevToolTheme mTheme = DevToolFragment.DevToolTheme.DARK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final TextView functionNumber = (TextView) findViewById(R.id.functions_number);
        final Spinner themeSpinner = (Spinner) findViewById(R.id.theme_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.debugkit_themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(adapter);
        themeSpinner.setOnItemSelectedListener(this);

        mSeekbar = (SeekBar) findViewById(R.id.seekBar);
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                functionNumber.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        functionNumber.setText(Integer.toString(mSeekbar.getProgress()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DevTool.Builder builder = new DevTool.Builder(ExampleActivity.this);

                if (mSeekbar != null) {
                    for (int i = 0; i < mSeekbar.getProgress(); i++) {
                        builder.addFunction(doSomeStuff());
                    }
                }

                builder.addFunction(new DebugFunction("Do some stuff") {
                    @Override
                    public String call() throws Exception {
                        return "This function has a title";
                    }
                }).addFunction(new DebugFunction.Clear())
                .addFunction(new DebugFunction.DumpSharedPreferences("Shared prefs", ExampleActivity.this, "toto"));

                builder.setTextSize(mTextSize)
                        .setTheme(mTheme)
                        .build();
                // After the tool has been built, you can set:
                // builder.getTool().changeConsoleTextSize(mTextSize);
            }
        });

        final DevTool.Builder builder = new DevTool.Builder(ExampleActivity.this);

        if (mSeekbar != null) {
            for (int i = 0; i < mSeekbar.getProgress(); i++) {
                builder.addFunction(doSomeStuff());
            }
        }

        builder.addFunction(new DebugFunction("Do some stuff") {
            @Override
            public String call() throws Exception {
                return "This function has a title";
            }
        });

        builder.setTextSize(mTextSize)
                .setTheme(mTheme)
                .build();
    }

    private DebugFunction doSomeStuff() {
        return new DebugFunction() {
            @Override
            public String call() throws Exception {
                // Do some kind of really debugging stuff...
                return "Some stuff was done.";
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mTheme = position == 0 ? DevToolFragment.DevToolTheme.DARK : DevToolFragment.DevToolTheme.LIGHT;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mTheme = DevToolFragment.DevToolTheme.DARK;
    }
}