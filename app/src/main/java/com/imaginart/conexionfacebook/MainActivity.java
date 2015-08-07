package com.imaginart.conexionfacebook;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.Collections;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private CallbackManager mCallbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        LoginButton btnLogin = (LoginButton) findViewById(R.id.btnLoginFacebook);
        Button btnPublish = (Button) findViewById(R.id.btnPublish);

        btnLogin.setReadPermissions("user_birthday");
        mCallbackManager = CallbackManager.Factory.create();
        btnLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "User id: " + loginResult.getAccessToken().getUserId());
                getUserInfo(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "facebook login error", e);
            }
        });
//        btnLogin.registerCallback();
        if (AccessToken.getCurrentAccessToken() != null) {
            getUserInfo(AccessToken.getCurrentAccessToken());

        }

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                publish();
            }
        });

    }




    private void getUserInfo(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                if (jsonObject != null) {
                    Log.d(TAG, "User info: " + jsonObject.toString());
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

    private void publish() {
        if (verifyPublishPermision()) {

            ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
            builder.setContentTitle("Title");
            ShareLinkContent shareLinkContent = builder.build();

            if (ShareDialog.canShow(ShareLinkContent.class)) {

                ShareDialog shareDialog = new ShareDialog(this);
                shareDialog.registerCallback(mCallbackManager, facebookCallback);
                shareDialog.show(shareLinkContent);
            } else {
                ShareApi.share(shareLinkContent, facebookCallback);

            }
        } else {
            LoginManager.getInstance().logInWithPublishPermissions(this,
                    Collections.singletonList("publish_actions"));
        }

    }


    private FacebookCallback facebookCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }

    };

    public boolean verifyPublishPermision() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();


        //if (accessToken != null) {
        return accessToken.getPermissions().contains("publish_actions");

        // }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
