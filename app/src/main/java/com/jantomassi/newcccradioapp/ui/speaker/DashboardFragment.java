package com.jantomassi.newcccradioapp.ui.speaker;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jantomassi.newcccradioapp.R;
import com.jantomassi.newcccradioapp.SpeakerItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private ArrayList<SpeakerItem> mSpeakerItem = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private LinearLayout tableLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_speaker, container, false);
        tableLayout = root.findViewById(R.id.page);
        mRequestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));

        jsonRun();
        return root;
    }

    private void jsonRun() {
        String url = "https://raw.githubusercontent.com/JanInInternet" +
                "/CCC_Radio/master/app/src/main/res/SpeakerAndReg.json";

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("speaker");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);

                                String creatorName = hit.getString("nome");
                                String imageUrl = hit.getString("imgUrl");
                                String likeCount = hit.getString("descrizione");

                                mSpeakerItem.add(new SpeakerItem(creatorName, imageUrl, likeCount));
                            }
                            elementGen(tableLayout);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        mRequestQueue.add(request);
    }

    private static float pixTodp(float px, Context context) {
        try {
            return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEVICE_STABLE);
        } catch (NoSuchFieldError e) {
            Log.e("pixTodp", e.toString());
            return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        }
    }

    private void elementGen(LinearLayout tableLayout) {
        for (SpeakerItem j : mSpeakerItem) {
            //Init horizontal Layout
            LinearLayout layout = new LinearLayout(tableLayout.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins((int) pixTodp(16f, tableLayout.getContext()),
                    (int) pixTodp(32f, tableLayout.getContext()),
                    (int) pixTodp(16f, tableLayout.getContext()),
                    (int) pixTodp(16f, tableLayout.getContext()));

            layout.setLayoutParams(lp);

            layout = button(layout, j.getImageUrl());
            layout = text(layout, j.getTitle());
            tableLayout.addView(layout);
        }
    }

    private LinearLayout button(LinearLayout tr, String imgUrl) {
        ImageView drawable = new ImageView(tableLayout.getContext());
        drawable.setMinimumWidth((int) pixTodp(200f, tableLayout.getContext()));
        drawable.setMinimumHeight((int) pixTodp(200f, tableLayout.getContext()));
        drawable.setMaxWidth((int) pixTodp(450f, tableLayout.getContext()));
        drawable.setMaxHeight((int) pixTodp(450f, tableLayout.getContext()));
        //Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(imgUrl)
                .resize((int) pixTodp(375f, tableLayout.getContext()), (int) pixTodp(375f, tableLayout.getContext()))
                .centerCrop()
                .into(drawable);
        tr.addView(drawable);
        return tr;
    }

    private LinearLayout text(LinearLayout tr, String text) {
        TextView tx = new TextView(tableLayout.getContext());
        tx.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 2f));
        tx.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        tx.setTextSize(18);
        tx.setText(text);
        tr.addView(tx);
        return tr;
    }
}
