package sxwang.me.ohmyyeelight.ui;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Shaoxing on 22/04/2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected <V extends View> V $(@IdRes int id) {
        return (V) findViewById(id);
    }
}
