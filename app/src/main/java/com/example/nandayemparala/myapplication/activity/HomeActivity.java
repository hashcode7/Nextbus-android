package com.example.nandayemparala.myapplication.activity;

/*
 * Created by Nanda Yemparala on 9/12/16.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.nandayemparala.myapplication.PrefsManager;
import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.api.AgencyListApi;
import com.example.nandayemparala.myapplication.api.PredictionsApi;
import com.example.nandayemparala.myapplication.application.App;
import com.example.nandayemparala.myapplication.fragment.PredictionsFragment_;
import com.example.nandayemparala.myapplication.model.Body;
import com.example.nandayemparala.myapplication.model.Route;
import com.example.nandayemparala.myapplication.model.Routes;
import com.example.nandayemparala.myapplication.model.Stop;
import com.orhanobut.hawk.Hawk;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseActivity {

    @ViewById(R.id.pager)
    ViewPager viewPager;
    @ViewById(R.id.sliding_tabs)
    TabLayout tabLayout;
    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @ViewById(R.id.left_drawer)
    ListView mDrawerList;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    public List<Route> routes = new ArrayList<>();
    public List<Fragment> framgents = new ArrayList<>();
    ProgressDialog progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(PrefsManager.isFirstStart()){
            startActivity(new Intent(this, IntroActivty.class));
        }
    }

    @AfterViews
    void loadFragment(){

        if(!PrefsManager.routesLoaded()){
            progressBar = ProgressDialog.show(this, "", "Please wait");
            initialDownload();
            return;
        }
        setSupportActionBar(toolbar);
//        mDrawerLayout.setStatusBarBackground(R.color.primary_dark);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer_desc, R.string.open_drawer_desc);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new String[]{"Predictions", "Favorites"}));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                App.showToast("Pos: "+position);
                mDrawerLayout.closeDrawers();
            }
        });
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


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

    private void initialDownload() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://webservices.nextbus.com")
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        AgencyListApi restInterface = retrofit.create(AgencyListApi.class);
        Call<Routes> routesCall = restInterface.getRoutes();

        routesCall.enqueue(new Callback<Routes>() {
            @Override
            public void onResponse(Call<Routes> call, Response<Routes> response) {
                List<Route> routes = response.body().routes;
                for (Route r : routes) {
                    try {
                        getHelper().getRouteDao().createOrUpdate(r);
                        List<Stop> stops = new ArrayList<>(r.getStops());
                        for (int i = 0; i < stops.size(); i++) {
                            Stop s = stops.get(i);
                            s.setRoute(r);
                            s.setStopNumber(i + 1);
                            getHelper().getStopDao().createOrUpdate(s);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                progressBar.cancel();
                Hawk.put("routes_loaded", true);
                loadFragment();
            }

            @Override
            public void onFailure(Call<Routes> call, Throwable t) {
                t.printStackTrace();
                progressBar.cancel();
            }
        });

    }
}
