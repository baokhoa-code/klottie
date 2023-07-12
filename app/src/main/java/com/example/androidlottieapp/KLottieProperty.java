/*
 * Copyright (C) 2020 - Amir Hossein Aghajari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.example.androidlottieapp;


public class KLottieProperty {
    final PropertyType type;
    final DynamicProperty<?> dynamicProperty;
    final int intValue;
    final float floatValue, floatValue2;

    KLottieProperty(PropertyType type, int value) {
        this.type = type;
        this.intValue = value;
        this.dynamicProperty = null;
        this.floatValue = 0;
        this.floatValue2 = 0;
    }

    KLottieProperty(PropertyType type, float value) {
        this.type = type;
        this.floatValue = value;
        this.dynamicProperty = null;
        this.intValue = 0;
        this.floatValue2 = 0;
    }

    KLottieProperty(PropertyType type, float value1, float value2) {
        this.type = type;
        this.floatValue = value1;
        this.floatValue2 = value2;
        this.dynamicProperty = null;
        this.intValue = 0;
    }

    KLottieProperty(PropertyType type, DynamicProperty<?> dynamicProperty) {
        this.type = type;
        this.dynamicProperty = dynamicProperty;
        this.intValue = 0;
        this.floatValue = 0;
        this.floatValue2 = 0;
    }

    /* Color property of Fill object */
    public static KLottieProperty fillColor(int color) {
        return new KLottieProperty(PropertyType.FillColor, color);
    }

    /* Dynamic color property of Fill object */
    public static KLottieProperty dynamicFillColor(DynamicProperty<Integer> dynamicProperty) {
        return new KLottieProperty(PropertyType.FillColor, dynamicProperty);
    }

    /* Opacity property of Fill object, [ 0 .. 100] */
    public static KLottieProperty fillOpacity(float value) {
        return new KLottieProperty(PropertyType.FillOpacity, value);
    }

    /* Dynamic opacity property of Fill object, [ 0 .. 100] */
    public static KLottieProperty dynamicFillOpacity(DynamicProperty<Float> dynamicProperty) {
        return new KLottieProperty(PropertyType.FillOpacity, dynamicProperty);
    }

    /* Color property of Stroke object */
    public static KLottieProperty strokeColor(int color) {
        return new KLottieProperty(PropertyType.StrokeColor, color);
    }

    /* Dynamic color property of Stroke object */
    public static KLottieProperty dynamicStrokeColor(DynamicProperty<Integer> dynamicProperty) {
        return new KLottieProperty(PropertyType.StrokeColor, dynamicProperty);
    }

    /* Opacity property of Stroke object, [ 0 .. 100] */
    public static KLottieProperty strokeOpacity(float value) {
        return new KLottieProperty(PropertyType.StrokeOpacity, value);
    }

    /* Dynamic opacity property of Stroke object, [ 0 .. 100] */
    public static KLottieProperty dynamicStrokeOpacity(DynamicProperty<Float> dynamicProperty) {
        return new KLottieProperty(PropertyType.StrokeOpacity, dynamicProperty);
    }

    /* Stroke with property of Stroke object */
    public static KLottieProperty strokeWidth(float value) {
        return new KLottieProperty(PropertyType.StrokeWidth, value);
    }

    /* Dynamic stroke with property of Stroke object */
    public static KLottieProperty dynamicStrokeWidth(DynamicProperty<Float> dynamicProperty) {
        return new KLottieProperty(PropertyType.StrokeWidth, dynamicProperty);
    }

    /* Transform Scale property of Layer and Group object, range[0 .. 360] in degrees*/
    public static KLottieProperty trRotation(float value) {
        return new KLottieProperty(PropertyType.TrRotation, value);
    }

    /* Dynamic transform Scale property of Layer and Group object, range[0 .. 360] in degrees*/
    public static KLottieProperty dynamicTrRotation(DynamicProperty<Float> dynamicProperty) {
        return new KLottieProperty(PropertyType.TrRotation, dynamicProperty);
    }

    /* Transform Opacity property of Layer and Group object, [ 0 .. 100] */
    public static KLottieProperty trOpacity(float value) {
        return new KLottieProperty(PropertyType.TrOpacity, value);
    }

    /* Dynamic transform Opacity property of Layer and Group object, [ 0 .. 100] */
    public static KLottieProperty dynamicTrOpacity(DynamicProperty<Float> dynamicProperty) {
        return new KLottieProperty(PropertyType.TrOpacity, dynamicProperty);
    }

    /* Transform Anchor property of Layer and Group object */
    public static KLottieProperty trAnchor(float x, float y) {
        return new KLottieProperty(PropertyType.TrAnchor, x, y);
    }

    /* Dynamic transform Anchor property of Layer and Group object */
    public static KLottieProperty dynamicTrAnchor(DynamicProperty<Float[]> dynamicProperty) {
        return new KLottieProperty(PropertyType.TrAnchor, dynamicProperty);
    }

    /* Transform Position property of Layer and Group object */
    public static KLottieProperty trPosition(float x, float y) {
        return new KLottieProperty(PropertyType.TrPosition, x, y);
    }

    /* Dynamic transform Position property of Layer and Group object */
    public static KLottieProperty dynamicTrPosition(DynamicProperty<Float[]> dynamicProperty) {
        return new KLottieProperty(PropertyType.TrPosition, dynamicProperty);
    }

    /* Transform Scale property of Layer and Group object, range[0 ..100] */
    public static KLottieProperty trScale(float w, float h) {
        return new KLottieProperty(PropertyType.TrScale, w, h);
    }

    /* Dynamic transform Scale property of Layer and Group object, range[0 ..100] */
    public static KLottieProperty dynamicTrScale(DynamicProperty<Float[]> dynamicProperty) {
        return new KLottieProperty(PropertyType.TrScale, dynamicProperty);
    }

    private static void apply(long ptr, String layer, KLottieProperty property) {
        if (property.dynamicProperty != null) {
            switch (property.type) {
                case FillColor:
                    KLottieNative.setDynamicLayerColor(ptr, layer, property.dynamicProperty);
                    break;
                case FillOpacity:
                    KLottieNative.setDynamicLayerFillOpacity(ptr, layer, property.dynamicProperty);
                    break;
                case StrokeColor:
                    KLottieNative.setDynamicLayerStrokeColor(ptr, layer, property.dynamicProperty);
                    break;
                case StrokeOpacity:
                    KLottieNative.setDynamicLayerStrokeOpacity(ptr, layer, property.dynamicProperty);
                    break;
                case StrokeWidth:
                    KLottieNative.setDynamicLayerStrokeWidth(ptr, layer, property.dynamicProperty);
                    break;
                case TrAnchor:
                    KLottieNative.setDynamicLayerTrAnchor(ptr, layer, property.dynamicProperty);
                    break;
                case TrOpacity:
                    KLottieNative.setDynamicLayerTrOpacity(ptr, layer, property.dynamicProperty);
                    break;
                case TrPosition:
                    KLottieNative.setDynamicLayerTrPosition(ptr, layer, property.dynamicProperty);
                    break;
                case TrRotation:
                    KLottieNative.setDynamicLayerTrRotation(ptr, layer, property.dynamicProperty);
                    break;
                case TrScale:
                    KLottieNative.setDynamicLayerTrScale(ptr, layer, property.dynamicProperty);
                    break;
            }
        } else {
            switch (property.type) {
                case FillColor:
                    KLottieNative.setLayerColor(ptr, layer, property.intValue);
                    break;
                case FillOpacity:
                    KLottieNative.setLayerFillOpacity(ptr, layer, property.floatValue);
                    break;
                case StrokeColor:
                    KLottieNative.setLayerStrokeColor(ptr, layer, property.intValue);
                    break;
                case StrokeOpacity:
                    KLottieNative.setLayerStrokeOpacity(ptr, layer, property.floatValue);
                    break;
                case StrokeWidth:
                    KLottieNative.setLayerStrokeWidth(ptr, layer, property.floatValue);
                    break;
                case TrAnchor:
                    KLottieNative.setLayerTrAnchor(ptr, layer, property.floatValue, property.floatValue2);
                    break;
                case TrOpacity:
                    KLottieNative.setLayerTrOpacity(ptr, layer, property.floatValue);
                    break;
                case TrPosition:
                    KLottieNative.setLayerTrPosition(ptr, layer, property.floatValue, property.floatValue2);
                    break;
                case TrRotation:
                    KLottieNative.setLayerTrRotation(ptr, layer, property.floatValue);
                    break;
                case TrScale:
                    KLottieNative.setLayerTrScale(ptr, layer, property.floatValue, property.floatValue2);
                    break;
            }
        }
    }

    private enum PropertyType {
        FillColor,
        FillOpacity,
        StrokeColor,
        StrokeOpacity,
        StrokeWidth,
        TrAnchor,
        TrOpacity,
        TrPosition,
        TrRotation,
        TrScale
    }

    public interface DynamicProperty<T> {
        T getValue(int frame);
    }

    static class PropertyUpdate {
        KLottieProperty property;
        String layer;

        PropertyUpdate(KLottieProperty property, String layer) {
            this.property = property;
            this.layer = layer;
        }

        void apply(long ptr) {
            KLottieProperty.apply(ptr, layer, property);
        }
    }
}
