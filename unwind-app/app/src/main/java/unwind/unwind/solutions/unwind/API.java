package unwind.unwind.solutions.unwind;

import android.content.Context;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class to handle calling the Unwind bot API.
 */
//TODO - Comment this all n stuff.
public class API {

    private OkHttpClient okHttpClient = new OkHttpClient();

    private static API mInstance = null;

    private Context context;

    public static synchronized API getInstance(Context context) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new API(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private API(Context context) {
        this.context = context;
    }

    public void postMessage(String userId, String message, Callback callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.format(Locale.UK, "{ id: %s, input: %s }", userId, message));
        Request request = new Request.Builder()
                .url(String.format(Locale.UK, "http://unwind.azurewebsites.net/api/add?id=%s&input=%s", userId, message))
                .post(body)
                .build();
            okHttpClient.newCall(request).enqueue(callback);

    }

    public static String[] cleanResults(String input) {
        String trimmedInput = input.replace("[", "");
        trimmedInput = trimmedInput.replace("]", "");

        String[] resultStrings = trimmedInput.split(",");

        for (int i = 0; i < resultStrings.length; i++) {
            resultStrings[i] = resultStrings[i].replace("\"", "");
        }

        return resultStrings;
    }

}
