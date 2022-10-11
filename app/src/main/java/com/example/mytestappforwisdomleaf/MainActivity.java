package com.example.mytestappforwisdomleaf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerViewAdapter.RecyclerViewClickListener listener;
    AlertDialog alertDialog;
    ArrayList<MainData> dataArrayList = new ArrayList<MainData>();
    private int page = 1;
    private int limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variables
        nestedScrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        //Set click listener
        setOnClickListener();

        //Initialize adapter
        recyclerViewAdapter = new RecyclerViewAdapter(dataArrayList, MainActivity.this, listener);
        //Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Set adapter
        recyclerView.setAdapter(recyclerViewAdapter);
        //Create get data method
        getData(page, limit);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //Check for last item
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        page++;
                        progressBar.setVisibility(View.VISIBLE);
                        getData(page, limit);
                    }
                }
            });
        }
    }

    private void setOnClickListener() {
        listener = new RecyclerViewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(dataArrayList.get(position).getDescription());
                builder.setTitle("Description");
                builder.setCancelable(true);
                alertDialog = builder.create();
                alertDialog.show();
            }
        };
    }

    private void getData(int page, int limit) {
        //Initialize retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://picsum.photos/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        //Create main Interface
        MainInterface mainInterface = retrofit.create(MainInterface.class);
        //Initialize call
        Call<String> call = mainInterface.STRING_CALL(page, limit);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Check condition
                if (response.isSuccessful() && response.body() != null) {
                    //Response success and not empty
                    progressBar.setVisibility(View.GONE);
                    try {
                        //Initialize json array
                        JSONArray jsonArray = new JSONArray(response.body());
                        //Parse json array
                        parseResult(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseResult(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                //Initialize json object
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //Set Data
                MainData data = new MainData();
                data.setImage(jsonObject.getString("download_url"));
                data.setTitle(jsonObject.getString("author"));
                if (i % 2 == 0)
                    data.setDescription(getResources().getString(R.string.description1));
                else
                    data.setDescription(getResources().getString(R.string.description2));
                dataArrayList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Initialize and set adapter
            recyclerViewAdapter = new RecyclerViewAdapter(dataArrayList, MainActivity.this, listener);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public void refreshRecyclerView(View view) {
        //page = 1;
        dataArrayList.clear();
        getData(page, limit);
        Toast.makeText(MainActivity.this, "Refreshed!", Toast.LENGTH_SHORT).show();
    }
}