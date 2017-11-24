package unwind.unwind.solutions.unwind;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Welcome slide.
        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.introduction_text1)
                .image(R.drawable.mi_ic_next) //TODO - Placeholder image.
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

        //Description slide 1.
        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.introduction_text2)
                .image(R.drawable.mi_ic_previous) //TODO - Placeholder image.
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

    }

}
