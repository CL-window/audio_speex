package com.cl.slack.speex.recoder;

import com.cl.slack.speex.speex.SpeexDecoder;

import java.io.File;

/**
 * Created by slack
 * on 17/2/14 下午1:37.
 */

public class SpeexPlayer {
    private String fileName = null;
    private SpeexDecoder speexdec = null;


    public SpeexPlayer(String fileName) {

        this.fileName = fileName;
        System.out.println(this.fileName);
        try {
            speexdec = new SpeexDecoder(new File(this.fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPlay() {
        RecordPlayThread rpt = new RecordPlayThread();

        Thread th = new Thread(rpt);
        th.start();
    }

    boolean isPlay = true;

    class RecordPlayThread extends Thread {

        public void run() {
            try {
                if (speexdec != null)
                    speexdec.decode();

            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    };
}
