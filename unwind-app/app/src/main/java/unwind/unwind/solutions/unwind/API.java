package unwind.unwind.solutions.unwind;

import android.content.Context;
import java.util.Locale;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Class to handle calling the Unwind bot API.
 */
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
        //RequestBody body = RequestBody.create(null, new byte[]{}); //(MediaType.parse("application/json; charset=utf-8"), "");
        Request request = new Request.Builder()
                .url(String.format(Locale.UK, "http://unwind.azurewebsites.net/api/add?id=%s&input=%s", userId, message))
                .get()
                .build();
            okHttpClient.newCall(request).enqueue(callback);

    }

    public static String cleanResults(String input) {
        String trimmedInput = input.replace("[", "");
        trimmedInput = trimmedInput.replace("]", "");

        String[] resultStrings = trimmedInput.split("\",");

        for (int i = 0; i < resultStrings.length; i++) {
            resultStrings[i] = resultStrings[i].replace("\"", "");
        }

        StringBuilder sb = new StringBuilder();
        for (String str: resultStrings) {
            sb.append(str).append("\n");
        }

        return sb.toString();
    }

}
