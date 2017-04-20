# Android DebugKit

DebugKit lib for Android allows you to use a fancy hovering debug tool to trigger some actions directly in the app. This tool is very useful to trigger some event at runtime, and to have a written feedback directly on your testing phone screen.

## Requirements

* Android SDK 15+

# Installation
```groovy
repositories {
    maven { url 'https://dl.bintray.com/nebneb/DebugKit' }
}

dependencies {
    compile 'com.hulab.android:debugkit:1.0.1'
}
```

# Usage

```java
final DevTool.Builder builder = new DevTool.Builder(mContext);

builder.addFunction(new DebugFunction() {
            @Override
            public String call() throws Exception {
                log("doing some stuff...");
                doSomeStuff();
                return "Some stuff was done.";
            }
        }).addFunction(new DebugFunction() {
            @Override
            public String call() throws Exception {
                log("doing some other stuff...");
                doSomeStuff();
                return "Some stuff was done.";
            }
        }).addFunction(new DebugFunction() {
            @Override
            public String call() throws Exception {
                log("doing some stuff again and again...");
                doSomeStuff();
                return "Some stuff was done.";
            }
        });                

// optional, DevToolFragment.DevToolTheme.DARK is set by default
builder.setTheme(DevToolFragment.DevToolTheme.DARK)
       .build();
```

## Result

Here we have 3 debug tools, from top to bottom:


* LIGHT theme with 5 defined functions
* Minified mode (clicking on the top left corner arrow)
* DARK theme with 3 defined functions and the logged text when clicking on each F1, F2 and F3
___
![Screenshot.png](https://github.com/hulab/debugkit/blob/master/resources/screenshot.png)
___
![dark theme](https://github.com/hulab/debugkit/blob/master/resources/theme_dark.gif)
![light theme](https://github.com/hulab/debugkit/blob/master/resources/theme_light.gif)
___

**Have fun!**
