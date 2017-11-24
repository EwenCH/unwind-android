package unwind.unwind.solutions.unwind;

import android.os.Bundle;
import android.app.Activity;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Welcome slide.
        addSlide(new SimpleSlide.Builder()
        .build());
    }

}
