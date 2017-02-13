package ru.bda.imagedribapp.view.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.bda.imagedribapp.R;
import ru.bda.imagedribapp.constants.APIData;
import ru.bda.imagedribapp.db.DBController;
import ru.bda.imagedribapp.entity.Shot;
import ru.bda.imagedribapp.net.APIService;
import ru.bda.imagedribapp.view.adapter.ShotRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private final  static int SIZE_LIST = 50;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ShotRecyclerAdapter mShotAdapter;
    private List<Shot> mShotList = new ArrayList<>();
    private DBController mDBController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBController = new DBController(this);
        initContent();
        if (isConnect()) {
            new  DeleteDBTask().execute();
        } else {
            new GetDBTask().execute();
        }
    }

    private boolean isConnect() {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
            return true;
        }
        return false;
    }

    private void initContent() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.yellow, R.color.red);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mShotAdapter = new ShotRecyclerAdapter(this, mShotList);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mShotAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

    private boolean getContentAPI () {
        APIService.getDribbbleShotService(APIData.CLIENT_ACCESS_TOKEN).getShots(100, false, "hidpi")
                .enqueue(new Callback<List<Shot>>() {
                    @Override
                    public void onResponse(Call<List<Shot>> call, Response<List<Shot>> response) {
                        if (response.body() != null) {
                            mShotList = customizedList(response.body(), SIZE_LIST);
                            mShotAdapter.setShotList(mShotList);
                            mShotAdapter.notifyDataSetChanged();
                            mRefreshLayout.setRefreshing(false);
                        }
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<List<Shot>> call, Throwable t) {
                        mRefreshLayout.setRefreshing(false);
                    }
                });
        return false;
    }

    private List<Shot> customizedList(List<Shot> preShotList, int size) {
        List<Shot> postShotList = new ArrayList<>();
        String hidpi = "hidpi";
        String teaser = "teaser";
        String normal = "normal";
        for (int i = 0; i < preShotList.size(); i++) {
            Shot shot = preShotList.get(i);
            if (!shot.getAnimated()) {
                Map<String, String> images = shot.getImages();
                String imagePath = "";
                if (images.get(hidpi) != null && !images.get(hidpi).equals("null")) {
                    imagePath = images.get(hidpi);
                } else if (images.get(normal) != null && !images.get(normal).equals("null")) {
                    imagePath = images.get(normal);
                } else if (images.get(teaser) != null && !images.get(teaser).equals("null")) {
                    imagePath = images.get(teaser);
                }
                shot.setImagePath(imagePath);
                shot.setDescription(textFormat(shot.getDescription()));
                if (!shot.getDescription().equals("null")){
                    postShotList.add(shot);
                }
                if (postShotList.size() == size) {
                    return postShotList;
                }
            }
        }
        return postShotList;
    }

    private String textFormat(String text) {
        StringBuilder sb = new StringBuilder();
        boolean addChar = true;
        if (text != null && !text.equals("null")) {
            for (char ch : text.toCharArray()) {
                if (ch == '<' && addChar) addChar = false;
                if (addChar) sb.append(ch);
                if (ch == '>') addChar = true;
            }
        } else {
            return "null";
        }
        return sb.toString();
    }

    @Override
    public void onRefresh() {
        if (isConnect()) {
            mRefreshLayout.setRefreshing(true);
            synchronized (DeleteDBTask.class) {
                new DeleteDBTask().execute();
            }
        } else {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class DeleteDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            synchronized (DBController.class) {
                mDBController.deleteData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getContentAPI();
        }
    }

    private class GetDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            synchronized (DBController.class) {
                mShotList = mDBController.getShotList();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mShotList != null && mShotList.size() > 0) {
                mShotAdapter.setShotList(mShotList);
                mShotAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "Don't have in database data", Toast.LENGTH_SHORT).show();
            }
            mRefreshLayout.setRefreshing(false);
        }
    }
}
