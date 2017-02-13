package ru.bda.imagedribapp;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.bda.imagedribapp.constants.APIData;
import ru.bda.imagedribapp.db.DBController;
import ru.bda.imagedribapp.entity.Shot;
import ru.bda.imagedribapp.net.APIService;
import ru.bda.imagedribapp.view.activity.MainActivity;
import ru.bda.imagedribapp.view.adapter.ShotRecyclerAdapter;

public class MainActivityTest extends ActivityInstrumentationTestCase2 {

    private MainActivity mActivity;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ShotRecyclerAdapter mShotAdapter;
    private List<Shot> mShotList = new ArrayList<>();
    private DBController mDBController;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mActivity = (MainActivity) getActivity();
        mRefreshLayout = (SwipeRefreshLayout) mActivity.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(mActivity);
        mRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.yellow, R.color.red);
        mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.recycler_view);
        mShotAdapter = new ShotRecyclerAdapter(mActivity, mShotList);
        mDBController = new DBController(mActivity);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testControlsCreated() throws Exception {
        assertNotNull(mActivity);
        assertNotNull(mRefreshLayout);
        assertNotNull(mRecyclerView);
        assertNotNull(mShotAdapter);
        assertNotNull(mDBController);
        assertNotNull(mShotList);
    }

    public void testControlsVisible() throws Exception {
        ViewAsserts.assertOnScreen(mRecyclerView.getRootView(), mRecyclerView);
        ViewAsserts.assertOnScreen(mRefreshLayout.getRootView(), mRefreshLayout);
    }

    public void testConnection() throws Exception {
        APIService.getDribbbleShotService(APIData.CLIENT_ACCESS_TOKEN).getShots(100, false, "hidpi")
                .enqueue(new Callback<List<Shot>>() {
                    @Override
                    public void onResponse(Call<List<Shot>> call, Response<List<Shot>> response) {
                        assertNotNull(response);
                    }

                    @Override
                    public void onFailure(Call<List<Shot>> call, Throwable t) {
                    }
                });

        assertNotNull(mDBController.getShotList());
    }
}
