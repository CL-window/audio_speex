Speex在Android上的实现
from : http://blog.csdn.net/xyz_lmn/article/details/8013490
        https://github.com/shangriDong/speex
1.创建新的android工程,直接包含c++代码，并创建cpp文件夹,使用 cmake 编译
2.speex源码目录下的libspeex和include目录及其子目录文件全部拷贝到$project/cpp目录下。
  Speex 1.2.0  This is the latest stable release of the Speex codec library.
3.cpp 下新建 CMakeLists.txt
4.在$project/cpp/include/speex/目录下新增speex_config_types.h文件
    这一步应该是映射Java数据类型的,在刚才拷贝过来的speex源码目录下面的include/speex文件夹下面
    有一个speex_config_types.h.in文件,可以在这个include/speex目录下面
    创建speex_config_types.h,把speex_config_types.h.in的内容拷贝过来,
    然后把@SIZE16@改成short,把@SIZE32@改成int就可以了

5.创建JNI包装类speex_jni.cpp，用来调用Speex中的C代码函数
6.编辑 build.gradle文件，可以编辑 c++ 代码 添加
     externalNativeBuild {
             cmake {
                 cppFlags ""
             }
         }
     }
     externalNativeBuild {
         cmake {
             path "src/main/cpp/CMakeLists.txt"
         }
     }

7.Speex 编码流程
    1、定义一个SpeexBits类型变量bits和一个Speex编码器状态变量enc_state。
    2、调用speex_bits_init(&bits)初始化bits。
    3、调用speex_encoder_init(&speex_nb_mode)来初始化enc_state。其中speex_nb_mode是SpeexMode类型的变量，表示的是窄带模式。还有speex_wb_mode表示宽带模式、speex_uwb_mode表示超宽带模式。
    4、调用函数int speex_encoder_ ctl(void *state, int request, void *ptr)来设定编码器的参数，其中参数state表示编码器的状态；参数request表示要定义的参数类型，如SPEEX_ GET_ FRAME_SIZE表示设置帧大小，SPEEX_ SET_QUALITY表示量化大小，这决定了编码的质量；参数ptr表示要设定的值。
    可通过speex_encoder_ctl(enc_state, SPEEX_GET_FRAME_SIZE, &frame_size) 和speex_encoder_ctl(enc_state, SPEEX_SET_QUALITY, &quality)来设定编码器的参数。
    5、初始化完毕后，对每一帧声音作如下处理：调用函数speex_bits_reset(&bits)再次设定SpeexBits，然后调用函数speex_encode(enc_state, input_frame, &bits)，参数bits中保存编码后的数据流。
    6、编码结束后，调用函数speex_bits_destroy (&bits)， speex_encoder_destroy (enc_state)来
8.解码流程
    1、 定义一个SpeexBits类型变量bits和一个Speex编码状态变量enc_state。
    2、 调用speex_bits_init(&bits)初始化bits。
    3、 调用speex_decoder_init (&speex_nb_mode)来初始化enc_state。
    4、 调用函数speex_decoder_ctl (void *state, int request, void *ptr)来设定编码器的参数。
    5、 调用函数 speex_decode(void *state, SpeexBits *bits, float *out)对参数bits中的音频数据进行解编码，参数out中保存解码后的数据流。
    6、 调用函数speex_bits_destroy(&bits), speex_ decoder_ destroy (void *state)来关闭和销毁SpeexBits和解码器。

9. 编译error ：
注：  libspeex/testenc.c， libspeex/testenc_uwb.c，libspeex/testenc_wb.c 包含 mian 需要注释掉
error1:
    libspeex/bits.c
    Error:(49, 1) error: unknown type name 'EXPORT'
solution1: EXPORT 　-->  JNIEXPORT  but need change too much
    also need  #include "jni.h"
    编译out ： speex/build/intermediates/cmake/debug/obj
solution2: use ndk,ndk need change include head , result is ok
    #include "../include/speex/speex_bits.h"
    编译out : speex/build/intermediates/ndk/debug/lib
error2:
    libspeex/arch.h
    Error:(65, 2) error: You now need to define either FIXED_POINT or FLOATING_POINT
solution: 注释掉那句话
