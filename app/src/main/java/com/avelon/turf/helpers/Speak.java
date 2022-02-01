package com.avelon.turf.helpers;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import com.avelon.turf.utils.Logger;

import java.util.Locale;

public class Speak implements TextToSpeech.OnInitListener {
    private Logger logger = new Logger(Speak.class);

    // --
    private final Activity activity;
    private TextToSpeech tts;
    private final Listen listen;

    public Speak(Activity activity, Listen listen) {
        logger.method("Speak()");
        this.activity = activity;
        this.listen = listen;
        
        Intent intent = new Intent();
        intent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        activity.startActivityForResult(intent, 8);
    }

    public void onActivityResult(int request, int result, Intent data) {
        logger.method("onActivityResult()", request, result, data.toString());
        
        if(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS != result) {
            logger.error("TTS Engine cannot be initiated");
            return;
        }

        tts = new TextToSpeech(activity, this);
    }

    @Override
    public void onInit(int status) {
        logger.method("init()");
        if (TextToSpeech.SUCCESS != status) {
            logger.error("Failed to init tts");
            return;
        }

        if (tts == null) {
            logger.error("tts is null");
            return;
        }

        int res = tts.setLanguage(Locale.ENGLISH);
        listen.done();
    }

    public void speak(String message) {
        if(tts != null)
            tts.speak(message, android.speech.tts.TextToSpeech.QUEUE_ADD, null);
    }

    public interface Listen {
        void done();
    }
}