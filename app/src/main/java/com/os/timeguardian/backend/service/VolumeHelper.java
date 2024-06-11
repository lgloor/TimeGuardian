package com.os.timeguardian.backend.service;

import android.content.Context;
import android.media.AudioManager;

// This class with his methods was created with the help of ChatGpt.
public class VolumeHelper {

    private Context context;
    private AudioManager audioManager;

    public VolumeHelper(Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setMaxVolume(int streamType) {
        if (audioManager != null) {
            int maxVolume = audioManager.getStreamMaxVolume(streamType);
            audioManager.setStreamVolume(streamType, maxVolume, AudioManager.FLAG_SHOW_UI);
        }
    }
}


/*
VolumeHelper volumeHelper = new VolumeHelper(context);
volumeHelper.setMaxVolume(AudioManager.STREAM_MUSIC);
 */