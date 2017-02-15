package com.cl.slack.speex.speex;

import android.util.Log;

import com.cl.slack.speex.writer.SpeexWriter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by slack
 * on 17/2/14 下午1:42.
 */

public class SpeexEncoder implements Runnable {
        private final static String TAG = "shangri";
        private final Object mutex = new Object();
        private Speex speex = new Speex();

        public static int encoder_packagesize = 1024;
        private byte[] processedData = new byte[encoder_packagesize];

        List<ReadData> list = null;
        private volatile boolean isRecording;
        private String fileName;

        public SpeexEncoder(String fileName) {
            super();
            speex.init();
            list = Collections.synchronizedList(new LinkedList<ReadData>());
            this.fileName = fileName;
        }

    public void run() {

        SpeexWriter fileWriter = new SpeexWriter(fileName);
        Thread consumerThread = new Thread((Runnable) fileWriter);
        fileWriter.setRecording(true);
        consumerThread.start();

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        int getSize = 0;
        while (this.isRecording()) {
            if (list.size() == 0) {
                Log.d(TAG, "no data need to do encode");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (list.size() > 0) {
                synchronized (mutex) {
                    ReadData rawdata = list.remove(0);
                    getSize = speex.encode(rawdata.ready, 0, processedData, rawdata.size);

                    Log.i(TAG, "after encode......................before=" + rawdata.size + " after=" + processedData.length + " getsize="
                            + getSize);
                }
                if (getSize > 0) {
                    fileWriter.putData(processedData, getSize);
                    Log.i(TAG, "............clear....................");
                    processedData = new byte[encoder_packagesize];
                }
            }
        }
        Log.d(TAG, "encode thread exit");
        fileWriter.setRecording(false);
    }

    /**
     * Recorder
     *
     * @param data
     * @param size
     */
    public void putData(short[] data, int size) {
        ReadData rd = new ReadData();
        synchronized (mutex) {
            rd.size = size;
            System.arraycopy(data, 0, rd.ready, 0, size);
            list.add(rd);
        }
    }

    public void setRecording(boolean isRecording) {
        synchronized (mutex) {
            this.isRecording = isRecording;
        }
    }

    public boolean isRecording() {
        synchronized (mutex) {
            return isRecording;
        }
    }

    class ReadData {
        private int size;
        private short[] ready = new short[encoder_packagesize];
    }
}
