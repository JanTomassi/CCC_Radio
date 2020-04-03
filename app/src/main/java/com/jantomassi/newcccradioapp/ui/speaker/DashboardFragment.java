package com.jantomassi.newcccradioapp.ui.speaker;

import android.os.Bundle;
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
        String url = "https://raw.githubusercontent.com/JanInInternet/CCC_Radio/master/app/src/main/res/Speaker%26Rec.json";

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

    private void elementGen(LinearLayout tableLayout) {
        for (SpeakerItem j : mSpeakerItem) {
            //Init horizontal Layout
            LinearLayout layout = new LinearLayout(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(0, 8, 0, 8);

            layout.setLayoutParams(lp);

            layout = button(layout, j.getImageUrl());
            layout = text(layout, j.getTitle());
            tableLayout.addView(layout);
        }
    }

    private LinearLayout button(LinearLayout tr, String imgUrl) {
        ImageView drawable = new ImageView(getContext());
        drawable.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        //Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(imgUrl)
                .resize(350, 350)
                .centerInside()
                .into(drawable);
        tr.addView(drawable);
        return tr;
    }

    private LinearLayout text(LinearLayout tr, String text) {
        TextView tx = new TextView(getContext());
        tx.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tx.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tx.setTextSize(18);
        tx.setText(text);
        tr.addView(tx);
        return tr;
    }
}
