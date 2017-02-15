/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2016, Jhuster <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/AudioDemo
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    WavFileHeader.java
 *  
 *  @version 1.0     
 *  @author  Jhuster
 *  @date    2016/03/19
 */
package com.dgk.myaudiodemo.audio;

public class WavFileHeader {

    public static final int WAV_FILE_HEADER_SIZE = 44;
    public static final int WAV_CHUNKSIZE_EXCLUDE_DATA = 36;

    public static final int WAV_CHUNKSIZE_OFFSET = 4;
    public static final int WAV_SUB_CHUNKSIZE1_OFFSET = 16;
    public static final int WAV_SUB_CHUNKSIZE2_OFFSET = 40;

    public String mChunkID = "RIFF";
    public int mChunkSize = 0;
    public String mFormat = "WAVE";

    public String mSubChunk1ID = "fmt ";
    public int mSubChunk1Size = 16;
    public short mAudioFormat = 1;
    public short mNumChannel = 1;   // 音频通道数量
    public int mSampleRate = 8000;  // 采样频率                                ÷8
    public int mByteRate = 0;       // 码率 = 采样频率 * 位宽 * 通道数 (bit/s) ------> Byte/s
    public short mBlockAlign = 0;
    public short mBitsPerSample = 8;// 位宽，一般为8bit或者16bit

    public String mSubChunk2ID = "data";
    public int mSubChunk2Size = 0;

    public WavFileHeader() {

    }

    public WavFileHeader(int sampleRateInHz, int bitsPerSample, int channels) {
        mSampleRate = sampleRateInHz;
        mBitsPerSample = (short) bitsPerSample;
        mNumChannel = (short) channels;
        mByteRate = mSampleRate * mNumChannel * mBitsPerSample / 8;
        mBlockAlign = (short) (mNumChannel * mBitsPerSample / 8);
    }
}

