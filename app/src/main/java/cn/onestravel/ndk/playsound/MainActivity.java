/*==============================================================================
FMOD Example Framework
Copyright (c), Firelight Technologies Pty, Ltd 2013-2019.
==============================================================================*/
package cn.onestravel.ndk.playsound;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import org.fmod.core.FmodUtils;

public class MainActivity extends Activity implements OnTouchListener, Runnable {
    private TextView mTxtScreen;
    private Thread mThread;
    private FmodUtils mFmodUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFmodUtils = FmodUtils.getInstance();
        // Create the text area
        mTxtScreen = new TextView(this);
        mTxtScreen.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f);
        mTxtScreen.setTypeface(Typeface.MONOSPACE);

        // Create the buttons
        Button[] buttons = new Button[9];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(this);
            buttons[i].setText(mFmodUtils.getButtonLabel(i));
            buttons[i].setOnTouchListener(this);
            buttons[i].setId(i);
        }

        // Create the button row layouts
        LinearLayout llTopRowButtons = new LinearLayout(this);
        llTopRowButtons.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout llMiddleRowButtons = new LinearLayout(this);
        llMiddleRowButtons.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout llBottomRowButtons = new LinearLayout(this);
        llBottomRowButtons.setOrientation(LinearLayout.HORIZONTAL);

        // Create the main view layout
        LinearLayout llView = new LinearLayout(this);
        llView.setOrientation(LinearLayout.VERTICAL);

        // Create layout parameters
        LayoutParams lpLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);

        // Set up the view hierarchy
        llTopRowButtons.addView(buttons[0], lpLayout);
        llTopRowButtons.addView(buttons[6], lpLayout);
        llTopRowButtons.addView(buttons[1], lpLayout);
        llMiddleRowButtons.addView(buttons[4], lpLayout);
        llMiddleRowButtons.addView(buttons[8], lpLayout);
        llMiddleRowButtons.addView(buttons[5], lpLayout);
        llBottomRowButtons.addView(buttons[2], lpLayout);
        llBottomRowButtons.addView(buttons[7], lpLayout);
        llBottomRowButtons.addView(buttons[3], lpLayout);
        llView.addView(mTxtScreen, lpLayout);
        llView.addView(llTopRowButtons);
        llView.addView(llMiddleRowButtons);
        llView.addView(llBottomRowButtons);

        setContentView(llView);

        // Request necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }

        org.fmod.FMOD.init(this);

        mThread = new Thread(this, "Example Main");
        mThread.start();

        mFmodUtils.setStateCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFmodUtils.setStateStart();
    }

    @Override
    protected void onStop() {
        mFmodUtils.setStateStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mFmodUtils.setStateDestroy();

        try {
            mThread.join();
        } catch (InterruptedException e) {
        }

        org.fmod.FMOD.close();

        super.onDestroy();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mFmodUtils.buttonDown(view.getId());
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            mFmodUtils.buttonUp(view.getId());
        }

        return true;
    }

    @Override
    public void run() {
        mFmodUtils.main(this);
    }

    public void updateScreen(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtScreen.setText(text);
            }
        });
    }


}
