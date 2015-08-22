package com.novagee.aidong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.novagee.aidong.R;

import com.arrownock.social.IAnSocialCallback;
import com.novagee.aidong.controller.UserManager;
import com.novagee.aidong.im.controller.IMManager;
import com.novagee.aidong.model.User;
import com.novagee.aidong.utils.Constant;
import com.novagee.aidong.utils.SpfHelper;

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 3000;
    private String payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        checkBundle();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                autoSignIn();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }

    private void autoSignIn() {
        if (SpfHelper.getInstance(this).hasSignIn()) {
            UserManager.getInstance(this).login(SpfHelper.getInstance(this).getMyUsername(),
                    SpfHelper.getInstance(this).getMyPwd(), new IAnSocialCallback() {
                        @Override
                        public void onFailure(JSONObject arg0) {
                            try {
                                String errorMsg = arg0.getJSONObject("meta").getString("message");
                                Toast.makeText(getBaseContext(), errorMsg, Toast.LENGTH_LONG).show();
                                goToLoginActivity();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                goToLoginActivity();
                            }
                        }

                        @Override
                        public void onSuccess(final JSONObject arg0) {
                            try {
                                JSONObject userJson = arg0.getJSONObject("response").getJSONObject("user");
                                User user = new User(userJson);
                                afterLogin(user);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                goToLoginActivity();
                            }
                        }
                    });
        } else {
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void afterLogin(User user) {
        IMManager.getInstance(this).connect(user.clientId);
        UserManager.getInstance(this).setCurrentUser(user);

        IMManager.getInstance(this).fetchAllRemoteTopic();
        UserManager.getInstance(this).fetchMyRemoteFriend(null);

        IMManager.getInstance(this).bindAnPush();

        Intent i = new Intent(this, MainActivity.class);
        if (payload != null) {
            i.putExtra(Constant.INTENT_EXTRA_KEY_PAYLOAD, payload);
        }
        startActivity(i);
        finish();
    }

    private void checkBundle() {
        if (getIntent().hasExtra(Constant.INTENT_EXTRA_KEY_PAYLOAD)) {
            payload = getIntent().getStringExtra(Constant.INTENT_EXTRA_KEY_PAYLOAD);
        }
    }
}
