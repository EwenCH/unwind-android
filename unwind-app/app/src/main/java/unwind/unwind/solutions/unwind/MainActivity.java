package unwind.unwind.solutions.unwind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity {

    public static final String PREF_KEY_FIRST_START = "com.unwind.app.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;

    //Components.
    private EditText inputText;
    private Button keyboardButton;
    private TextView botResponse;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining some animations.
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(500);
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(500);

        //Set the status bar colour.
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //Find out if this is the first time the app is being opened.
        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        //If it is, call the introduction activity.
        if (firstStart) {
            Intent intent = new Intent(this, MainIntroActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }

        userID = UUID.randomUUID().toString();

        //Initialising components.
        keyboardButton = findViewById(R.id.keyboardButton);
        inputText = findViewById(R.id.inputText);
        botResponse = findViewById(R.id.botResponse);

        //Empty message to get first message.
        API.getInstance(getApplicationContext()).postMessage(userID, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connection Error.",
                                Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                final String result = API.cleanResults(body);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        botResponse.startAnimation(out);
                        botResponse.setText(result);
                        botResponse.startAnimation(in);
                    }
                });
                Log.i("API", result.toString());
            }
        });

        inputText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String text = inputText.getText().toString();
                    API.getInstance(getApplicationContext()).postMessage(userID, text, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Connection Error.",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String body = response.body().string();
                            final String result = API.cleanResults(body);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    botResponse.setAnimation(out);
                                    botResponse.setText(result);
                                    botResponse.startAnimation(in);

                                    inputText.setText("");
                                    inputText.requestFocus();
                                    inputText.setFocusableInTouchMode(true);
                                    inputText.setCursorVisible(false);

                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(inputText, InputMethodManager.SHOW_FORCED);
                                    inputText.setCursorVisible(true);
                                }
                            });
                            Log.i("API", result.toString());
                        }
                    });
                    inputText.setCursorVisible(false);
                    return true;
                }
                inputText.setCursorVisible(false);
                return false;
            }
        });

        //Handle the button being pressed.
        keyboardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputText.requestFocus();
                inputText.setFocusableInTouchMode(true);
                inputText.setCursorVisible(true);

                inputText.setText("");
                inputText.startAnimation(in);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputText, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {

            //If the introduction activity has finished, then save the first start boolean
            //as false.
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO - Temporary Dev options, remove for production.
        //Set the first start variable to be true if the menu item is selected.
        if (item.getItemId() == R.id.reset_first_start_menu) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, true)
                    .apply();
            return true;
        } else if (item.getItemId() == R.id.see_user_id) {
            Toast.makeText(this, userID,
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
