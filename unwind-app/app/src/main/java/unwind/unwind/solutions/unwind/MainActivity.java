package unwind.unwind.solutions.unwind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * The main activity of the Unwind app - comprising of the input and output for the chat bot.
 */
public class MainActivity extends Activity {

    //Variables used to check if the app has been opened before or not.
    public static final String PREF_KEY_FIRST_START = "com.unwind.app.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;

    //Android widgets.
    private EditText inputText;
    private ImageButton keyboardButton;
    private TextView botResponse;
    private ImageView botLogo;
    private ImageView loadingCircle;

    //User ID of the current user, used in the Unwind API.
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining some animations to make the app feel less 'sharp'.
        final Animation start = new AlphaAnimation(1.0f, 0.0f);
        start.setDuration(0);
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(500);
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(500);

        //Find out if this is the first time the app is being opened.
        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        //If it is, call the introduction activity.
        if (firstStart) {
            Intent intent = new Intent(this, MainIntroActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }

        //Generate the random user ID for this session.
        userID = UUID.randomUUID().toString();

        //Initialising the android widgets.
        keyboardButton = findViewById(R.id.keyboardButton);
        inputText = findViewById(R.id.inputText);
        botResponse = findViewById(R.id.botResponse);
        botLogo = findViewById(R.id.botImageView);
        loadingCircle = findViewById(R.id.loadingCircleImageView);

        //Load some of the images/GIFS and fade the loading circle onto the screen.
        botLogo.startAnimation(start);
        keyboardButton.startAnimation(start);
        loadingCircle.startAnimation(start);
        Glide.with(this).load(R.drawable.breathe).into(botLogo);
        Glide.with(this).load(R.drawable.ripple).into(loadingCircle);
        loadingCircle.startAnimation(in);

        //Empty message API call to get first message from the Unwind bot.
        API.getInstance(getApplicationContext()).postMessage(userID, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //If the API call failed, create a toast informing the connection error and
                        //shut the app down.
                        Toast.makeText(getApplicationContext(), R.string.conn_fail,
                                Toast.LENGTH_LONG).show();
                        finishAndRemoveTask();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //If the API call responds, get the response and clean the results.
                String body = response.body().string();
                final String result = API.cleanResults(body);

                //Set the text and handle animations on the UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingCircle.startAnimation(out);
                        loadingCircle.setVisibility(View.GONE);

                        botResponse.startAnimation(start);
                        botResponse.setText(result);
                        botResponse.startAnimation(in);

                        //Load the other images.
                        botLogo.startAnimation(in);
                        keyboardButton.setAnimation(in);
                    }
                });
                Log.i("API", result.toString());
            }
        });

        inputText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //If the event is a key-down event on the "enter" button, send the message typed
                //by the user to the API.
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String text = inputText.getText().toString();

                    API.getInstance(getApplicationContext()).postMessage(userID, text,
                            new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //If the API call fails, inform the user with a toast.
                                    Toast.makeText(getApplicationContext(), R.string.conn_fail,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //Clean the message returned by the API.
                            String body = response.body().string();
                            final String result = API.cleanResults(body);

                            //Handle changing the UI (animations/text etc.) on the UI thread.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    botResponse.setAnimation(out);
                                    botResponse.setText(result);
                                    botResponse.startAnimation(in);

                                    //Remove the text just typed by the user.
                                    inputText.setText("");
                                    inputText.requestFocus();
                                    inputText.setFocusableInTouchMode(true);
                                    inputText.setCursorVisible(false);

                                    //Make the keyboard show.
                                    InputMethodManager imm = (InputMethodManager)
                                            getSystemService(Context.INPUT_METHOD_SERVICE);
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

        //Handle the keyboard button being pressed.
        keyboardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Set the focus on the input text box.
                inputText.requestFocus();
                inputText.setFocusableInTouchMode(true);
                inputText.setCursorVisible(true);

                //Make sure the input text is cleared.
                inputText.setText("");
                inputText.startAnimation(in);

                //Show the keyboard.
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    /**
     * Method to handle an options menu item being selected. Mostly used for dev tools.
     * @param item The item selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Set the first start variable to be true if the menu item is selected.
        if (item.getItemId() == R.id.reset_first_start_menu) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, true)
                    .apply();
            return true;
        //Show the user ID in a small toast.
        } else if (item.getItemId() == R.id.see_user_id) {
            Toast.makeText(this, userID,
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
