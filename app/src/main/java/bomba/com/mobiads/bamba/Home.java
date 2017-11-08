package bomba.com.mobiads.bamba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bomba.com.mobiads.bamba.fragment.MyAccount;
import bomba.com.mobiads.bamba.fragment.MyTuneFragment;
import bomba.com.mobiads.bamba.fragment.Overview;

public class Home extends BaseActivity {

    public TabLayout tabLayout;
    public static ViewPager viewPager;
    private Overview overviewFragment;
//    private Overview overviewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


//        setupTypeface(this);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                setupTabIcons();
            }
        });

//        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorMyAccount));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if(position == 0){
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                    setStautsColor(getResources().getColor(R.color.colorPurple));
                }else if(position == 1){
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.colorMyTone));
                    setStautsColor(getResources().getColor(R.color.colorMyTone));
                }else if(position == 2){
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.colorMyAccount));
                    setStautsColor(getResources().getColor(R.color.colorMyAccount));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setStautsColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.75;
            getWindow().setStatusBarColor(Color.HSVToColor(hsv));
        }
    }

    public void setupTabIcons(){


        View overviewView = getLayoutInflater().inflate(R.layout.custom_tabs, null);
        TextView overviewTitle = (TextView) overviewView.findViewById(R.id.title_text);
        ImageView iv    = (ImageView) overviewView.findViewById(R.id.icon);
//        iv.setColorFilter(this.getResources().getColor(R.color.colorGrey));
        Glide.with(this).load(R.drawable.overview_icon).into(iv);
//        overviewTitle.setTypeface(Roboto_Light);
        overviewTitle.setText("Overview");
        tabLayout.getTabAt(0).setCustomView(overviewView);




        View serviceView = getLayoutInflater().inflate(R.layout.custom_tabs, null);
        TextView serviceTitle = (TextView) serviceView.findViewById(R.id.title_text);
        ImageView iv1    = (ImageView) serviceView.findViewById(R.id.icon);
//        iv1.setColorFilter(this.getResources().getColor(R.color.colorGrey));
        Glide.with(this).load(R.mipmap.tune_icon).into(iv1);
//        serviceTitle.setTypeface(Roboto_Light);
        serviceTitle.setText("My Tunes");
        tabLayout.getTabAt(1).setCustomView(serviceView);


        View accountView = getLayoutInflater().inflate(R.layout.custom_tabs, null);
        TextView accountTitle = (TextView) accountView.findViewById(R.id.title_text);
        ImageView iv2    = (ImageView) accountView.findViewById(R.id.icon);
//        iv2.setColorFilter(this.getResources().getColor(R.color.colorGrey));
        Glide.with(this).load(R.mipmap.account_icon).into(iv2);
//        accountTitle.setTypeface(Roboto_Light);
        accountTitle.setText("My Account");
        tabLayout.getTabAt(2).setCustomView(accountView);



    }


    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        overviewFragment = new Overview();
        adapter.addFragment(overviewFragment, "OVERVIEW", "0");
        adapter.addFragment(new MyTuneFragment(), "TONES", "1");
        adapter.addFragment(new MyAccount(), "ACCOUNT","2");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, String id) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
            return null;
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for(Fragment fragment : fragmentList){
                if(fragment instanceof OnBackPressedListener){
                    ((OnBackPressedListener)fragment).onBackPressed();
                }
            }
        }
    }

}
