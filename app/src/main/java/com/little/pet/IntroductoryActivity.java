package com.little.pet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;

public class IntroductoryActivity extends AppCompatActivity {

    ImageView logo,splashImg;
    LottieAnimationView lottieAnimationView;

    private static final int NUM_PAGES = 3;
    private ViewPager viewPager;

    Animation anim;

    private static  int SPLASH_TIME_OUT = 4000;
    SharedPreferences mSharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);
        //logo = findViewById(R.id.logo);
        splashImg = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.splash);


        //viewPager.setAdapter(pagerAdapter);

        anim = AnimationUtils.loadAnimation(this, R.anim.o_b_anim);
//        viewPager.startAnimation(anim);

        splashImg.animate().translationY(-4000).setDuration(4000).setStartDelay(6000);
        //logo.animate().translationY(-450).setDuration(4000).setStartDelay(6000);
        lottieAnimationView.animate().translationY(2000).setDuration(4000).setStartDelay(6000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSharedPref = getSharedPreferences("SharedPref",MODE_PRIVATE);
                boolean isFirstTime = mSharedPref.getBoolean("firstTime",true);

                startActivity(new Intent(IntroductoryActivity.this, Informacion1Activity.class));

                if (isFirstTime){
                    SharedPreferences.Editor editor =mSharedPref.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();


                }
                else {
                    Intent intent = new Intent(IntroductoryActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }


}