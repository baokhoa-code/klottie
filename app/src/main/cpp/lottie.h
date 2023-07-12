#ifndef KLOTTIE_APP_LOTTIE_H
#define KLOTTIE_APP_LOTTIE_H

typedef struct LottieWrapper{
public:
    static void convertToCanvasFormat(rlottie::Surface &s);
};

typedef struct LottieInfo{
    ~LottieInfo() {
    }

public:
    std::unique_ptr<rlottie::Animation> animation;
    size_t frameCount = 0;
    int32_t fps = 30;
    int32_t duration = 0;
};

#endif
