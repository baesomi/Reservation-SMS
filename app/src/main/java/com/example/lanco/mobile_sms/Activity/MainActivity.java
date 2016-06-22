package com.example.lanco.mobile_sms.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.lanco.mobile_sms.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.github.clans.fab.FloatingActionMenu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //AppEventsLogger.activateApp(this);

        // 가장 상단의 바 - 제목이나, 메뉴를 제공
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // fragmentPagerAdapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // 뷰페이저 어뎁터 처리
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // 탭레이아웃 정의
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // 플로팅 버튼처리
        FloatingActionMenu fab = (FloatingActionMenu) findViewById(R.id.menu);
        com.github.clans.fab.FloatingActionButton fab1 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.menu_item1);
        com.github.clans.fab.FloatingActionButton fab2 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.menu_item2);

        fab.setMenuButtonColorNormalResId(R.color.colorFloat);
        fab1.setColorNormalResId(R.color.colorFloat);
        fab2.setColorNormalResId(R.color.colorFloat);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WriteActivity.class));
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WriteGroupActivity.class));
            }
        });
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.sample", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {}
        catch (NoSuchAlgorithmException e) {
        }*/
        //hash key 받아오기

        Intent intent = getIntent();
        if(intent.getIntExtra("MotificationIntent", 0) == 1)
            tabLayout.getTabAt(2).select();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final int iconRes[] = {
                R.drawable.home_mini, R.drawable.send_mini, R.drawable.history_two_mini, R.drawable.setting_mini
        };

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    HomeActivity home = new HomeActivity();
                    return home;
                case 1:
                    SendActivity send = new SendActivity();
                    return send;
                case 2:
                    HistoryActivity his = new HistoryActivity();
                    return his;
                case 3:
                    SettingActivity set = new SettingActivity();
                    return set;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            SpannableStringBuilder sb= new SpannableStringBuilder(" ");; // space added before text for convenience
            ImageSpan span;
            Drawable drawable;

            switch (position) {
                case 0:
                    drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.home_mini);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                case 1:
                    drawable = ContextCompat.getDrawable(getApplicationContext(),iconRes[position]);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                case 2:
                    drawable = ContextCompat.getDrawable(getApplicationContext(),iconRes[position]);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                case 3:
                    drawable = ContextCompat.getDrawable(getApplicationContext(),iconRes[position]);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
            }
            return null;
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //빽(취소)키가 눌렸을때 종료여부를 묻는 다이얼로그 띄움
        if((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
            d.setTitle("문자를 부탁해!");
            d.setMessage("정말 종료 하시겠습니꺄?");
            d.setIcon(R.mipmap.sms_icon);

            d.setPositiveButton("예",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                }
            });

            d.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            d.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
