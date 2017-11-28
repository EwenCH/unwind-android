package unwind.unwind.solutions.unwind;

import android.os.Bundle;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Class that creates and shows the introduction slides when the app is first opened on a user's
 * phone. Created using the Material Intro library.
 */
public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Welcome slide.
        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.introduction_text1)
                .image(R.drawable.happy)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

        //Description slide 1.
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title1)
                .description(R.string.introduction_text2)
                .image(R.drawable.relax)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

        //Description slide 2.
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title2)
                .description(R.string.introduction_text3)
                .image(R.drawable.lock)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .scrollable(false)
                .build());

    }

}
