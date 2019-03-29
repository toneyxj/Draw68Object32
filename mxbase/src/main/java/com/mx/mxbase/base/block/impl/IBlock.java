package com.mx.mxbase.base.block.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public interface IBlock {

    void attachActivity(Activity activity);

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onSaveInstanceState(Bundle outState );

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    boolean onBackPressed();
}
