package jayjaybinks.github.com.concertsforyou;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import de.umass.lastfm.User;
import de.umass.lastfm.cache.FileSystemCache;


import static jayjaybinks.github.com.concertsforyou.LastFMUtility.LASTFM_CALLER;

/**
 * A login screen that offers login via email/password.
 */
public class LastFMActivity extends AppCompatActivity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserConnectTask mAuthTask = null;

    // UI references.
    private EditText mLastFMUsername;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_fm);
        // Set up the login form.
        mLastFMUsername = findViewById(R.id.lastfm_username_input);
        mLastFMUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptConnect();
                    return true;
                }
                return false;
            }
        });

        Button mConnectButton = findViewById(R.id.lastfm_connect_button);
        mConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptConnect();
            }
        });

        Button mSkipButton = findViewById(R.id.lastfm_skip_button);
        mSkipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    @Override
    public void finish(){
        Intent intent = new Intent(this, ConcertsActivity.class);
        startActivity(intent);
    }

    /**
     * Attempts to connect the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptConnect() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLastFMUsername.setError(null);

        // Store values at the time of the login attempt.
        String lastFMUsername = mLastFMUsername.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (!isUsernameValid(lastFMUsername)) {
            mLastFMUsername.setError(getString(R.string.error_invalid_username));
            focusView = mLastFMUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserConnectTask(lastFMUsername, this.getApplicationContext());
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return !TextUtils.isEmpty(username) && !username.contains(" ");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
     class UserConnectTask extends AsyncTask<Void, Void, Boolean> {


        private final String lastFMUsername;
        private final Context context;

        UserConnectTask(String lastFMUsername, Context context) {
            this.context = context;
            this.lastFMUsername = lastFMUsername;
            //TODO this is not the reight place
            LASTFM_CALLER.setUserAgent("ConcertsForYou");
            LASTFM_CALLER.setCache(new FileSystemCache(
                    new File(context.getFilesDir().getPath())));
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            User user = User.getInfo(lastFMUsername, getString(R.string.api_key));
            if(user.getName().equals(lastFMUsername) == false){
                return false;
            }

             SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.preference_lastfm_username), lastFMUsername);
            editor.commit();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mLastFMUsername.setError(getString(R.string.error_invalid_username));
                mLastFMUsername.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

