#include <jni.h>
#include <android/bitmap.h>
#include <cstring>
#include "inc/rlottie.h"
#include "lz4/lz4.h"
#include <unistd.h>
#include <condition_variable>
#include <atomic>
#include <thread>
#include <map>
#include <sys/stat.h>
#include <utime.h>
#include "c_utils.h"
#include "gif/gif.h"
#include "lottie.h"

extern "C" {
using namespace rlottie;

jlong Java_com_example_androidlottieapp_KLottieNative_createInfoByJson(JNIEnv *env, jclass clazz,
                                                                       jstring json, jstring name,
                                                                       jintArray data) {
    LottieInfo *info = new LottieInfo();

    char const *jsonString = env->GetStringUTFChars(json, 0);
    char const *nameString = env->GetStringUTFChars(name, 0);
    info->animation = rlottie::Animation::loadFromData(jsonString, nameString);

    if (jsonString != 0) {
        env->ReleaseStringUTFChars(json, jsonString);
    }
    if (nameString != 0) {
        env->ReleaseStringUTFChars(name, nameString);
    }
    if (info->animation == nullptr) {
        delete info;
        return 0;
    }
    info->frameCount = info->animation->totalFrame();
    info->fps = (int) info->animation->frameRate();
    info->duration = (int) info->animation->duration();

    jint *dataArr = env->GetIntArrayElements(data, 0);
    if (dataArr != nullptr) {
        dataArr[0] = (int) info->frameCount;
        dataArr[1] = (int) info->animation->frameRate();
        dataArr[2] = 0;
        env->ReleaseIntArrayElements(data, dataArr, 0);
    }
    return (jlong) (intptr_t) info;
}

void
Java_com_example_androidlottieapp_KLottieNative_releaseInfo(JNIEnv *env, jclass clazz, jlong ptr) {
    if (ptr == NULL) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    delete info;
}

jint Java_com_example_androidlottieapp_KLottieNative_getFrame(JNIEnv *env, jclass clazz, jlong ptr,
                                                              jint frame, jobject bitmap, jint w,
                                                              jint h, jint stride) {
    if (ptr == NULL || bitmap == nullptr) {
        return -1;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;

    void *pixels;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0) {
        Surface surface((uint32_t *) pixels, (size_t) w, (size_t) h, (size_t) stride);
        info->animation->renderSync((size_t) frame, surface);
        LottieWrapper::convertToCanvasFormat(surface);
        AndroidBitmap_unlockPixels(env, bitmap);
    }
    return frame;
}

void LottieWrapper::convertToCanvasFormat(Surface &s) {
    uint8_t *buffer = reinterpret_cast<uint8_t *>(s.buffer());
    uint32_t totalBytes = s.height() * s.bytesPerLine();

    for (int i = 0; i < totalBytes; i += 4) {
        unsigned char r = buffer[i + 2];
        unsigned char g = buffer[i + 1];
        unsigned char b = buffer[i];

        buffer[i] = r;
        buffer[i + 1] = g;
        buffer[i + 2] = b;
    }
}

jint Java_com_example_androidlottieapp_KLottieNative_getLayersCount(JNIEnv *env, jclass clazz,
                                                                    jlong ptr) {
    if (ptr == NULL) {
        return 0;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    return info->animation->layers().size();
}

jobjectArray
Java_com_example_androidlottieapp_KLottieNative_getLayerData(JNIEnv *env, jclass clazz, jlong ptr,
                                                             jint index) {
    if (ptr == NULL) {
        return NULL;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;

    jobjectArray ret = (jobjectArray) env->NewObjectArray(4, env->FindClass("java/lang/String"),
                                                          env->NewStringUTF(""));

    std::tuple<std::string, int, int, int> layer = info->animation->layers().at(index);
    env->SetObjectArrayElement(ret, 0, env->NewStringUTF(std::get<0>(layer).c_str()));
    env->SetObjectArrayElement(ret, 1,
                               env->NewStringUTF(std::to_string(std::get<1>(layer)).c_str()));
    env->SetObjectArrayElement(ret, 2,
                               env->NewStringUTF(std::to_string(std::get<2>(layer)).c_str()));
    env->SetObjectArrayElement(ret, 3,
                               env->NewStringUTF(std::to_string(std::get<3>(layer)).c_str()));
    return (ret);
}

jint Java_com_example_androidlottieapp_KLottieNative_getMarkersCount(JNIEnv *env, jclass clazz,
                                                                     jlong ptr) {
    if (ptr == NULL) {
        return 0;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    return info->animation->markers().size();
}

jobjectArray
Java_com_example_androidlottieapp_KLottieNative_getMarkerData(JNIEnv *env, jclass clazz, jlong ptr,
                                                              jint index) {
    if (ptr == NULL) {
        return NULL;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;

    jobjectArray ret = (jobjectArray) env->NewObjectArray(3, env->FindClass("java/lang/String"),
                                                          env->NewStringUTF(""));

    std::tuple<std::string, int, int> markers = info->animation->markers().at(index);
    env->SetObjectArrayElement(ret, 0, env->NewStringUTF(std::get<0>(markers).c_str()));
    env->SetObjectArrayElement(ret, 1,
                               env->NewStringUTF(std::to_string(std::get<1>(markers)).c_str()));
    env->SetObjectArrayElement(ret, 2,
                               env->NewStringUTF(std::to_string(std::get<2>(markers)).c_str()));
    return (ret);
}

void
Java_com_example_androidlottieapp_KLottieNative_setLayerColor(JNIEnv *env, jclass clazz, jlong ptr,
                                                              jstring layer, jint color) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::FillColor>(layerString,
                                                   rlottie::Color(((color >> 16) & 0xff) / 255.0f,
                                                                  ((color >> 8) & 0xff) / 255.0f,
                                                                  ((color) & 0xff) / 255.0f));
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerColor(JNIEnv *env, jclass clazz,
                                                                          jlong ptr, jstring layer,
                                                                          jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)Ljava/lang/Integer;");

    info->animation->setValue<Property::FillColor>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                jobject colorObj = env->CallObjectMethod(store_Wlistener, mth_update,
                                                         (jint) (info.curFrame()));
                jclass cls = env->GetObjectClass(colorObj);
                jmethodID integerToInt = env->GetMethodID(cls, "intValue", "()I");
                jint color = env->CallIntMethod(colorObj, integerToInt);

                return rlottie::Color(((color >> 16) & 0xff) / 255.0f,
                                      ((color >> 8) & 0xff) / 255.0f,
                                      ((color) & 0xff) / 255.0f);
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerStrokeColor(JNIEnv *env, jclass clazz,
                                                                         jlong ptr, jstring layer,
                                                                         jint color) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::StrokeColor>(layerString,
                                                     rlottie::Color(((color >> 16) & 0xff) / 255.0f,
                                                                    ((color >> 8) & 0xff) / 255.0f,
                                                                    ((color) & 0xff) / 255.0f));
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerStrokeColor(JNIEnv *env,
                                                                                jclass clazz,
                                                                                jlong ptr,
                                                                                jstring layer,
                                                                                jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)Ljava/lang/Integer;");

    info->animation->setValue<Property::StrokeColor>(layerString,
                                                     [mth_update, store_Wlistener, env](
                                                             const FrameInfo &info) {
                                                         jobject colorObj = env->CallObjectMethod(
                                                                 store_Wlistener, mth_update,
                                                                 (jint) (info.curFrame()));
                                                         jclass cls = env->GetObjectClass(colorObj);
                                                         jmethodID integerToInt = env->GetMethodID(
                                                                 cls, "intValue", "()I");
                                                         jint color = env->CallIntMethod(colorObj,
                                                                                         integerToInt);

                                                         return rlottie::Color(
                                                                 ((color >> 16) & 0xff) / 255.0f,
                                                                 ((color >> 8) & 0xff) / 255.0f,
                                                                 ((color) & 0xff) / 255.0f);
                                                     });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerFillOpacity(JNIEnv *env, jclass clazz,
                                                                         jlong ptr, jstring layer,
                                                                         jfloat value) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::FillOpacity>(layerString, (float) value);
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerFillOpacity(JNIEnv *env,
                                                                                jclass clazz,
                                                                                jlong ptr,
                                                                                jstring layer,
                                                                                jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)Ljava/lang/Float;");

    info->animation->setValue<Property::FillOpacity>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                jobject obj = env->CallObjectMethod(store_Wlistener, mth_update,
                                                    (jint) (info.curFrame()));
                jclass cls = env->GetObjectClass(obj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");
                jfloat value = env->CallFloatMethod(obj, FloatToF);

                return (float) value;
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void
Java_com_example_androidlottieapp_KLottieNative_setLayerStrokeOpacity(JNIEnv *env, jclass clazz,
                                                                      jlong ptr, jstring layer,
                                                                      jfloat value) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::StrokeOpacity>(layerString, (float) value);
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerStrokeOpacity(JNIEnv *env,
                                                                                  jclass clazz,
                                                                                  jlong ptr,
                                                                                  jstring layer,
                                                                                  jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)Ljava/lang/Float;");

    info->animation->setValue<Property::StrokeOpacity>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                jobject obj = env->CallObjectMethod(store_Wlistener, mth_update,
                                                    (jint) (info.curFrame()));
                jclass cls = env->GetObjectClass(obj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");
                jfloat value = env->CallFloatMethod(obj, FloatToF);

                return (float) value;
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerStrokeWidth(JNIEnv *env, jclass clazz,
                                                                         jlong ptr, jstring layer,
                                                                         jfloat value) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::StrokeWidth>(layerString, (float) value);
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerStrokeWidth(JNIEnv *env,
                                                                                jclass clazz,
                                                                                jlong ptr,
                                                                                jstring layer,
                                                                                jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)Ljava/lang/Float;");

    info->animation->setValue<Property::StrokeWidth>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                jobject obj = env->CallObjectMethod(store_Wlistener, mth_update,
                                                    (jint) (info.curFrame()));
                jclass cls = env->GetObjectClass(obj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");
                jfloat value = env->CallFloatMethod(obj, FloatToF);

                return (float) value;
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerTrRotation(JNIEnv *env, jclass clazz,
                                                                        jlong ptr, jstring layer,
                                                                        jfloat value) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::TrRotation>(layerString, (float) value);
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void
Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerTrRotation(JNIEnv *env, jclass clazz,
                                                                          jlong ptr, jstring layer,
                                                                          jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)Ljava/lang/Float;");

    info->animation->setValue<Property::TrRotation>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                jobject obj = env->CallObjectMethod(store_Wlistener, mth_update,
                                                    (jint) (info.curFrame()));
                jclass cls = env->GetObjectClass(obj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");
                jfloat value = env->CallFloatMethod(obj, FloatToF);

                return (float) value;
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerTrOpacity(JNIEnv *env, jclass clazz,
                                                                       jlong ptr, jstring layer,
                                                                       jfloat value) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::TrOpacity>(layerString, (float) value);
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void
Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerTrOpacity(JNIEnv *env, jclass clazz,
                                                                         jlong ptr, jstring layer,
                                                                         jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)Ljava/lang/Float;");

    info->animation->setValue<Property::TrOpacity>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                jobject obj = env->CallObjectMethod(store_Wlistener, mth_update,
                                                    (jint) (info.curFrame()));
                jclass cls = env->GetObjectClass(obj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");
                jfloat value = env->CallFloatMethod(obj, FloatToF);

                return (float) value;
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerTrAnchor(JNIEnv *env, jclass clazz,
                                                                      jlong ptr, jstring layer,
                                                                      jfloat x, jfloat y) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::TrAnchor>(layerString, Point((float) x, (float) y));
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void
Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerTrAnchor(JNIEnv *env, jclass clazz,
                                                                        jlong ptr, jstring layer,
                                                                        jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)[Ljava/lang/Float;");

    info->animation->setValue<Property::TrAnchor>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                auto obj = (jobjectArray) env->CallObjectMethod(store_Wlistener, mth_update,
                                                                (jint) (info.curFrame()));

                jobject xObj = env->GetObjectArrayElement(obj, 0);
                jobject yObj = env->GetObjectArrayElement(obj, 1);
                jclass cls = env->GetObjectClass(xObj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");

                jfloat x = env->CallFloatMethod(xObj, FloatToF);
                jfloat y = env->CallFloatMethod(yObj, FloatToF);

                return Point((float) x, (float) y);
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerTrPosition(JNIEnv *env, jclass clazz,
                                                                        jlong ptr, jstring layer,
                                                                        jfloat x, jfloat y) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::TrPosition>(layerString, Point((float) x, (float) y));
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void
Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerTrPosition(JNIEnv *env, jclass clazz,
                                                                          jlong ptr, jstring layer,
                                                                          jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)[Ljava/lang/Float;");

    info->animation->setValue<Property::TrPosition>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                auto obj = (jobjectArray) env->CallObjectMethod(store_Wlistener, mth_update,
                                                                (jint) (info.curFrame()));

                jobject xObj = env->GetObjectArrayElement(obj, 0);
                jobject yObj = env->GetObjectArrayElement(obj, 1);
                jclass cls = env->GetObjectClass(xObj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");

                jfloat x = env->CallFloatMethod(xObj, FloatToF);
                jfloat y = env->CallFloatMethod(yObj, FloatToF);

                return Point((float) x, (float) y);
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void Java_com_example_androidlottieapp_KLottieNative_setLayerTrScale(JNIEnv *env, jclass clazz,
                                                                     jlong ptr, jstring layer,
                                                                     jfloat w, jfloat h) {
    if (ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);
    info->animation->setValue<Property::TrScale>(layerString, Size((float) w, (float) h));
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

void
Java_com_example_androidlottieapp_KLottieNative_setDynamicLayerTrScale(JNIEnv *env, jclass clazz,
                                                                       jlong ptr, jstring layer,
                                                                       jobject listener) {
    if (listener == NULL || ptr == NULL || layer == nullptr) {
        return;
    }
    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;
    char const *layerString = env->GetStringUTFChars(layer, 0);

    jweak store_Wlistener = env->NewWeakGlobalRef(listener);
    jclass lclazz = env->GetObjectClass(store_Wlistener);
    jmethodID mth_update = env->GetMethodID(lclazz, "getValue", "(I)[Ljava/lang/Float;");

    info->animation->setValue<Property::TrScale>
            (layerString, [mth_update, store_Wlistener, env](const FrameInfo &info) {
                auto obj = (jobjectArray) env->CallObjectMethod(store_Wlistener, mth_update,
                                                                (jint) (info.curFrame()));

                jobject xObj = env->GetObjectArrayElement(obj, 0);
                jobject yObj = env->GetObjectArrayElement(obj, 1);
                jclass cls = env->GetObjectClass(xObj);
                jmethodID FloatToF = env->GetMethodID(cls, "floatValue", "()F");

                jfloat x = env->CallFloatMethod(xObj, FloatToF);
                jfloat y = env->CallFloatMethod(yObj, FloatToF);

                return Size((float) x, (float) y);
            });
    if (layerString != 0) {
        env->ReleaseStringUTFChars(layer, layerString);
    }
}

}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_androidlottieapp_KLottieNative_getFramerate(JNIEnv *env, jclass clazz, jlong ptr) {
    if (ptr == NULL) {
        return 0;
    }

    LottieInfo *info = (LottieInfo *) (intptr_t) ptr;

    return info->fps;
}