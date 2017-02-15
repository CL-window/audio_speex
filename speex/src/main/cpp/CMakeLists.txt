# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.
cmake_minimum_required(VERSION 3.4.1)

# 在执行CMAKE时打印消息 注意大小写
# message([SEND_ERROR | STATUS | FATAL_ERROR] "message to display" ...)
message(STATUS "Source Directory: ${CMAKE_CURRENT_SOURCE_DIR}")

message(STATUS "Android ABI: ${ANDROID_ABI}")
message(STATUS "Android API LEVEL: ${ANDROID_NATIVE_API_LEVEL}")

# 添加头文件目录,指定头文件与库文件路径 include_directories与link_directories 可以多次调用以设置多个路径
include_directories("include")

file(GLOB_RECURSE ALL_PROJ_SRC "libspeex/*.c" "speex_jni.cpp")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.
add_library( # Sets the name of the library.
             audio_speex
             SHARED
             ${ALL_PROJ_SRC})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build. 查找库所在目录
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       audio_speex

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
