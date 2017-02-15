//
// Created by slack on 17/2/14.
//

# include <jni.h>
#include <string.h>
#include <unistd.h>

#include <include/speex/speex.h>

#include <android/log.h>
#include <stdio.h>

#define TAG    "slack" // 这个是自定义的LOG的标识
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__) // 定义LOGD类型

static int codec_open = 0;

static int dec_frame_size;
static int enc_frame_size;

static SpeexBits ebits, dbits;
void *enc_state;
void *dec_state;

static JavaVM *gJavaVM;

extern "C" {


JNIEXPORT jint JNICALL
Java_com_cl_slack_speex_speex_Speex_open(JNIEnv *env, jobject instance, jint compression) {

    int tmp;

    if (codec_open++ != 0)
        return (jint)0;

    speex_bits_init(&ebits);
    speex_bits_init(&dbits);

    enc_state = speex_encoder_init(&speex_nb_mode);
    dec_state = speex_decoder_init(&speex_nb_mode);
    tmp = compression;
    speex_encoder_ctl(enc_state, SPEEX_SET_QUALITY, &tmp);
    speex_encoder_ctl(enc_state, SPEEX_GET_FRAME_SIZE, &enc_frame_size);
    speex_decoder_ctl(dec_state, SPEEX_GET_FRAME_SIZE, &dec_frame_size);

    return (jint)0;

}

JNIEXPORT jint JNICALL
Java_com_cl_slack_speex_speex_Speex_getFrameSize(JNIEnv *env, jclass type) {

    if (!codec_open)
        return 0;
    return (jint)enc_frame_size;

}

JNIEXPORT jint JNICALL
Java_com_cl_slack_speex_speex_Speex_decode(JNIEnv *env, jobject instance, jbyteArray encoded,
                                           jshortArray lin, jint size) {

    jbyte buffer[dec_frame_size];
    jshort output_buffer[dec_frame_size];
    jsize encoded_length = size;

    if (!codec_open)
        return 0;

    LOGD("########## encoded_length = %d", encoded_length);
    LOGD("########## dec_frame_size = %d", dec_frame_size);
    env->GetByteArrayRegion(encoded, 0, encoded_length, buffer);
    for (int i = 0; i < encoded_length; i++)
    {
        LOGD("########## buffer = %c", buffer[i]);
    }

    speex_bits_read_from(&dbits, (char *)buffer, encoded_length);
    speex_decode_int(dec_state, &dbits, output_buffer);
    env->SetShortArrayRegion(lin, 0, dec_frame_size,
                             output_buffer);

    return (jint)dec_frame_size;

}

JNIEXPORT jint JNICALL
Java_com_cl_slack_speex_speex_Speex_encode(JNIEnv *env, jobject instance, jshortArray lin,
                                           jint offset, jbyteArray encoded, jint size) {
    jshort buffer[enc_frame_size];
    jbyte output_buffer[enc_frame_size];
    int nsamples = (size-1)/enc_frame_size + 1;
    int i, tot_bytes = 0;

    if (!codec_open)
        return 0;

    speex_bits_reset(&ebits);

    for (i = 0; i < nsamples; i++) {
        env->GetShortArrayRegion(lin, offset + i*enc_frame_size, enc_frame_size, buffer);
        speex_encode_int(enc_state, buffer, &ebits);
    }

    tot_bytes = speex_bits_write(&ebits, (char *)output_buffer,
                                 enc_frame_size);
    env->SetByteArrayRegion(encoded, 0, tot_bytes,
                            output_buffer);

    return (jint)tot_bytes;
}

JNIEXPORT void JNICALL
Java_com_cl_slack_speex_speex_Speex_close(JNIEnv *env, jobject instance) {

    if (--codec_open != 0)
        return;

    speex_bits_destroy(&ebits);
    speex_bits_destroy(&dbits);
    speex_decoder_destroy(dec_state);
    speex_encoder_destroy(enc_state);

}

}