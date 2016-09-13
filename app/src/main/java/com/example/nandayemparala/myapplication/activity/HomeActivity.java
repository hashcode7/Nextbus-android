package com.example.nandayemparala.myapplication.activity;/*
 * Created by Nanda Yemparala on 9/12/16.
 */

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.fragment.PredictionsFragment_;
import com.example.nandayemparala.myapplication.model.Route;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseActivity {

    @ViewById(R.id.pager)
    ViewPager viewPager;
    @ViewById(R.id.sliding_tabs)
    TabLayout tabLayout;

    public List<Route> routes = new ArrayList<>();
    public List<Fragment> framgents = new ArrayList<>();

    @AfterViews
    void loadFragment(){
        try{
            routes = getHelper().getRouteDao().queryForAll();

            for(Route route: routes){
                framgents.add(PredictionsFragment_.builder().routeTag(route.getTag()).build());
            }

            PagerAdapter pagerAdapter  = new PagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);

            tabLayout.setupWithViewPager(viewPager);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return framgents.get(i);
        }

        @Override
        public int getCount() {
            return framgents.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return routes.get(position).title;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

    }
}
