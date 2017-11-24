package unwind.unwind.solutions.unwind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static final String PREF_KEY_FIRST_START = "com.unwind.app.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;

    //Components.
    private EditText inputText;
    private Button sendButton;
    private ListView responseList;

    List<String> messages = new ArrayList<>();
    ArrayAdapter<String> adapter;

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

        //Initialising components.
        sendButton = findViewById(R.id.sendButton);
        inputText = findViewById(R.id.inputText);
        responseList = findViewById(R.id.responseList);

        //Setting up the adapter for the list view.
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messages);
        responseList.setAdapter(adapter);

        //Handle the button being pressed.
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String text = inputText.getText().toString();
                messages.add(text);
                adapter.notifyDataSetChanged();
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
        //TODO - Temporary Dev option, remove for production.
        //Set the first start variable to be true if the menu item is selected.
        if (item.getItemId() == R.id.reset_first_start_menu) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, true)
                    .apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
