package com.jxlc.tajiproject.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jxlc.tajiproject.R;
import com.jxlc.tajiproject.ui.layout.ConfigureLayout;
import com.jxlc.tajiproject.ui.layout.ConstructionSiteLayout;
import com.jxlc.tajiproject.ui.layout.DiagramLayout;
import com.jxlc.tajiproject.ui.layout.HistoryLayout;
import com.jxlc.tajiproject.ui.layout.SettingLayout;
import com.jxlc.tajiproject.ui.layout.TowerCraneLayout;
import com.unity3d.player.UnityPlayer;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

import static com.jxlc.tajiproject.R.id.container;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentFragment extends Fragment implements ScreenShotable {
    public static final String CLOSE = "Close";
    public static final String TOWERCRANE = "TowerCrane";
    public static final String CONSTRUCTIONSITE = "ConstructionSite";
    public static final String CONFIGURE = "Configure";
    public static final String HISTORY = "History";
    public static final String DIAGRAM = "Diagram";
    public static final String SETTING = "Setting";

    private FrameLayout containerView;
    protected String loadLayout;
    private Bitmap bitmap;
    protected UnityPlayer mUnityPlayer;

    public static ContentFragment newInstance(String layoutName) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Integer.class.getName(), layoutName);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    public void setUnityPlayer(UnityPlayer player) {
        if (player != null) {
            mUnityPlayer = player;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLayout = getArguments().getString(Integer.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = (FrameLayout) view.findViewById(container);
        switch (loadLayout) {
            case TOWERCRANE:
                if (mUnityPlayer != null) {
                    TowerCraneLayout tcLayout = new TowerCraneLayout(getActivity(), null, mUnityPlayer);
                    this.containerView.addView(tcLayout);
                } else {
                    TowerCraneLayout tcLayout = new TowerCraneLayout(getActivity());
                    this.containerView.addView(tcLayout);
                }
                break;
            case CONSTRUCTIONSITE:
                if (mUnityPlayer != null) {
                    ConstructionSiteLayout csLayout = new ConstructionSiteLayout(getActivity(), null, mUnityPlayer);
                    this.containerView.addView(csLayout);
                } else {
                    ConstructionSiteLayout csLayout = new ConstructionSiteLayout(getActivity());
                    this.containerView.addView(csLayout);
                }
                break;
            case CONFIGURE:
                this.containerView.addView(new ConfigureLayout(getActivity()));
                break;
            case HISTORY:
                this.containerView.addView(new HistoryLayout(getActivity()));
                break;
            case DIAGRAM:
                this.containerView.addView(new DiagramLayout(getActivity()));
                break;
            case SETTING:
                this.containerView.addView(new SettingLayout(getActivity()));
                break;
            default:
                break;
        }
    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                ContentFragment.this.bitmap = bitmap;
            }
        };
        thread.start();
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }


}

