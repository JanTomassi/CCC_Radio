package com.jantomassi.newcccradioapp.ui.radio;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.jantomassi.newcccradioapp.MediaService;
import com.jantomassi.newcccradioapp.R;

public class RadioFragment extends Fragment {

    public static MutableLiveData<Integer> audioStreamCtlBtnImg = new MutableLiveData<>();
    public static MediaService mediaService = new MediaService();
    private RadioViewModel radioViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        radioViewModel = ViewModelProviders.of(this).get(RadioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_radio, container, false);

        final ImageButton audioStreamCtlBtn = root.findViewById(R.id.audioStreamCtlBtn);

        audioStreamCtlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MediaService.mediaPlayer.isPlaying()) {
                    mediaService.mediaPause();
                } else {
                    mediaService.mediaPlay();
                }
            }
        });

        audioStreamCtlBtnImg.observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(@NonNull Integer integer) {
                try {
                    audioStreamCtlBtn.setBackgroundResource(audioStreamCtlBtnImg.getValue());
                    audioStreamCtlBtn.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    Log.e("AudioBtnImg", String.format("No Value in audioStreamCtlBtnImg, Error\n %s", e));
                }
            }
        });

        return root;
    }
}