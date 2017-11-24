package unwind.unwind.solutions.unwind;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity {

    public static final String PREF_KEY_FIRST_START = "com.unwind.app.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;

    //Components.
    private EditText inputText;
    private Button sendButton;
    private ListView responseList;

    List<String> messages = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        sendButton = findViewById(R.id.sendButton);
        inputText = findViewById(R.id.inputText);
        responseList = findViewById(R.id.responseList);

        //Setting up the adapter for the list view.
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messages);
        responseList.setAdapter(adapter);

        /*
        API.getInstance(getApplicationContext()).postMessage(userID, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                messages.add("Connection error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                String[] results = API.cleanResults(body);
                messages.add(results.toString());
            }
        });
        adapter.notifyDataSetChanged();
        */

        //Handle the button being pressed.
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String text = inputText.getText().toString();
                messages.add(text);
                adapter.notifyDataSetChanged();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        API.getInstance(getApplicationContext()).postMessage(userID, text, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messages.add("Connection error.");
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String body = response.body().string();
                                final String[] results = API.cleanResults(body);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messages.add(results.toString());
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                Log.i("API", results.toString());
                            }
                        });
                    }
                });
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
