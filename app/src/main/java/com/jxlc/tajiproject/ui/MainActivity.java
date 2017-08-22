/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxlc.tajiproject.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.algorithm.AntiCollisionAlgorithm;
import com.jxlc.tajiproject.bean.EnvironmentInfo;
import com.jxlc.tajiproject.bean.TowerCraneInfo;
import com.jxlc.tajiproject.transmitter.Transmitter;
import com.jxlc.tajiproject.ui.fragment.ContentFragment;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

import static com.jxlc.tajiproject.ui.fragment.ContentFragment.CONFIGURE;
import static com.jxlc.tajiproject.ui.fragment.ContentFragment.CONSTRUCTIONSITE;
import static com.jxlc.tajiproject.ui.fragment.ContentFragment.DIAGRAM;
import static com.jxlc.tajiproject.ui.fragment.ContentFragment.HISTORY;
import static com.jxlc.tajiproject.ui.fragment.ContentFragment.SETTING;
import static com.jxlc.tajiproject.ui.fragment.ContentFragment.TOWERCRANE;


public class MainActivity extends AppCompatActivity implements ViewAnimator.ViewAnimatorListener {
    private DrawerLayout mainLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ViewAnimator viewAnimator;
    private List<SlideMenuItem> menuItems = new ArrayList<>();
    private LinearLayout menuItemsContainer;

    private String curLayout = TOWERCRANE;
    private ContentFragment contentFragment;
    protected UnityPlayer mUnityPlayer;
    public Map<String, String> PAGENAME_MAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBX_8888);
        mUnityPlayer = new UnityPlayer(this);

        setContentView(R.layout.activity_main);
        contentFragment = ContentFragment.newInstance(curLayout);
        contentFragment.setUnityPlayer(mUnityPlayer);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();
        mainLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mainLayout.setScrimColor(Color.TRANSPARENT);
        menuItemsContainer = (LinearLayout) findViewById(R.id.left_drawer);
        menuItemsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.closeDrawers();
            }
        });
        initValues();
        setActionBar();
        createMenuList();

        Transmitter.getInstance(MainActivity.this).start();
    }

    // set the globe init values here
    private void initValues() {
        PAGENAME_MAP = new HashMap<>();
        PAGENAME_MAP.put(TOWERCRANE, getResources().getString(R.string.page_towercrane));
        PAGENAME_MAP.put(CONSTRUCTIONSITE, getResources().getString(R.string.page_constructionsite));
        PAGENAME_MAP.put(CONFIGURE, getResources().getString(R.string.page_configure));
        PAGENAME_MAP.put(HISTORY, getResources().getString(R.string.page_history));
        PAGENAME_MAP.put(DIAGRAM, getResources().getString(R.string.page_diagram));
        PAGENAME_MAP.put(SETTING, getResources().getString(R.string.page_setting));

        EnvironmentInfo enInfo = EnvironmentInfo.getInstance();
        enInfo.setWindSpeed(6.5f);
        enInfo.setTemperature(16.6f);
        enInfo.setConstructionSiteWidth(800);
        enInfo.setConstructionSiteHeight(800);

        List<TowerCraneInfo> demoList = new ArrayList<>();
        TowerCraneInfo info1 = TowerCraneInfo.getDemoInfo();
        info1.setIdentifier(1);
        info1.setAngle(120);
        demoList.add(info1);
        TowerCraneInfo info2 = TowerCraneInfo.getDemoInfo();
        info2.setIdentifier(2);
        info2.setCoordinateX(info2.getCoordinateX() + info2.getFrontArmLength() + 30);
        info2.setCoordinateY(info2.getCoordinateY() - info2.getFrontArmLength() + 40);
        info2.setAngle(240);
        demoList.add(info2);
        TowerCraneInfo info3 = TowerCraneInfo.getDemoInfo();
        info3.setIdentifier(3);
        info3.setCoordinateX(400);
        info3.setCoordinateY(400);
        info3.setFrontArmLength(80);
        info3.setRearArmLength(12);
        info3.setAngle(270);
        demoList.add(info3);
        TowerCraneInfo info4 = TowerCraneInfo.getDemoInfo();
        info4.setIdentifier(4);
        info4.setCoordinateX(475);
        info4.setCoordinateY(440);
        info4.setFrontArmLength(80);
        info4.setRearArmLength(12);
        info4.setAngle(100);
        demoList.add(info4);
        AntiCollisionAlgorithm.getInstance().setTowerCraneList(demoList);
        AntiCollisionAlgorithm.getInstance().setCheckTowerId(demoList.get(0).getIdentifier());
    }

    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.icn_close);
        menuItems.add(menuItem0);
        SlideMenuItem menuItem1 = new SlideMenuItem(TOWERCRANE, R.drawable.icn_1);
        menuItems.add(menuItem1);
        SlideMenuItem menuItem2 = new SlideMenuItem(CONSTRUCTIONSITE, R.drawable.icn_2);
        menuItems.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(CONFIGURE, R.drawable.icn_3);
        menuItems.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(HISTORY, R.drawable.icn_4);
        menuItems.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(DIAGRAM, R.drawable.icn_5);
        menuItems.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(SETTING, R.drawable.icn_6);
        menuItems.add(menuItem6);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        viewAnimator = new ViewAnimator<>(this, menuItems, contentFragment, mainLayout, this, height / 7);
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(PAGENAME_MAP.get(curLayout));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mainLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                menuItemsContainer.removeAllViews();
                menuItemsContainer.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && menuItemsContainer.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mainLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_about:
                new SweetAlertDialog(this)
                        .setTitleText("About")
                        .setContentText("欢迎使用 智能塔机三维监控系统平台\n"
                            + "Android客户端\n\n"
                            + "Copyright (C)2016-2017, 武汉市匠心领创信息科技有限公司.\n"
                            + "All rights reserved.\n\n"
                            + "DO NOT COPY AND/OR REDISTRIBUTE WITHOUT PERMISSION\n"
                            + "Any Questions, Welcome to Contact cloudcastle@outlook.com")
                        .show();
                return true;
            case R.id.action_close:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition, String name) {
        // make Animation
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        findViewById(R.id.content_overlay).setBackground(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();

        // switch layout
        curLayout = name;
        getSupportActionBar().setTitle(PAGENAME_MAP.get(name));
        ContentFragment newFragment = ContentFragment.newInstance(name);
        if (name.equals(TOWERCRANE) || name.equals(CONSTRUCTIONSITE)) {
            mUnityPlayer = new UnityPlayer(this);
            newFragment.setUnityPlayer(mUnityPlayer);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newFragment).commit();
        return newFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        String itemName = slideMenuItem.getName();
        switch (itemName) {
            case ContentFragment.CLOSE:
                return screenShotable;
            default:
                return replaceFragment(screenShotable, position, itemName);
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        mainLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        menuItemsContainer.addView(view);
    }

    // Resume Unity
    @Override protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            finish();
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
