package com.graduation.a3ltreq.ontheroad.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.graduation.a3ltreq.ontheroad.Adapter.ProviderAdapter;
import com.graduation.a3ltreq.ontheroad.MapsActivity;
import com.graduation.a3ltreq.ontheroad.R;
import com.graduation.a3ltreq.ontheroad.app.AppConfig;
import com.graduation.a3ltreq.ontheroad.model.Provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RescueFragment extends Fragment implements ProviderAdapter.ProviderOnClickHandler {
    private static final String PROVIDERS = "providers";
    public static final String PROVIDER_DETAILS = "detail_provider";

    private ProviderAdapter mAdapter;
    private Provider provider;
    private Context context;
    private static final String TAG = RescueFragment.class.getSimpleName();


    public RescueFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_provider, container, false);


        RecyclerView recyclerView = rootView.findViewById(R.id.p_recycler_view);
        ArrayList<Provider> mProviders = new ArrayList<>();

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = new ProviderAdapter(mProviders, context, this);
        recyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PROVIDERS)) {
                mProviders = savedInstanceState.getParcelableArrayList(PROVIDERS);
                mAdapter.add(mProviders);
            } else {
                getProviders();
            }
        } else {
            getProviders();
        }


        return rootView;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Provider> providers = mAdapter.getProviders();
        if (providers != null && !providers.isEmpty()) {
            outState.putParcelableArrayList(PROVIDERS, providers);
        }
    }

    private void getProviders() {

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROVIDERS, new Response.Listener<String>() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.d(TAG, R.string.response + response);
                ArrayList<Provider> mPs = new ArrayList<>();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("status");
                    if (error == 200) {
                        JSONArray array = jObj.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jsonObject = array.getJSONObject(i);
                            provider = new Provider(jsonObject);
                            mPs.add(provider);

                        }
                        if (mPs != null) {
                            if (mAdapter != null) {
                                mAdapter.add(mPs);
                            }
                        }

                    } else {


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

        };

        // Adding request to request queue
        queue.add(strReq);
    }

    @Override
    public void onClickProvider(Provider provider) {
        Intent intent = new Intent(getContext(),MapsActivity.class);
        intent.putExtra(PROVIDER_DETAILS, provider);
        startActivity(intent);

    }
}
