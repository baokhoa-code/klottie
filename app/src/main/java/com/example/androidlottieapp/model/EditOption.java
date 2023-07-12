package com.example.androidlottieapp.model;

import androidx.annotation.Nullable;

public class EditOption {
    public final static int SPEED = 0;
    public final static int BACK_GROUND = 1;
    public final static int TRIM = 2;
    public final static int OPTION = 3;

    public int type = -1;
    public String changedLayer = "";
    public String changedProperty = "";

    public int originValueInt = -1;
    public float originValueFloat = -1;
    public float originValueFloat1 = -1;
    public float originValueFloat2 = -1;

    public int oldValueInt = -1;
    public float oldValueFloat = -1;
    public float oldValueFloat1 = -1;
    public float oldValueFloat2 = -1;

    public int newValueInt = -1;
    public float newValueFloat = -1;
    public float newValueFloat1 = -1;
    public float newValueFloat2 = -1;
    public EditOptionListener listener;

    public EditOption() {
    }


    public EditOption(int type, EditOptionListener listener) {
        this.type = type;
        this.listener = listener;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getChangedLayer() {
        return changedLayer;
    }

    public void setChangedLayer(String changedLayer) {
        this.changedLayer = changedLayer;
    }

    public String getChangedProperty() {
        return changedProperty;
    }

    public void setChangedProperty(String changedProperty) {
        this.changedProperty = changedProperty;
    }

    public int getOriginValueInt() {
        return originValueInt;
    }

    public void setOriginValueInt(int originValueInt) {
        this.originValueInt = originValueInt;
    }

    public float getOriginValueFloat() {
        return originValueFloat;
    }

    public void setOriginValueFloat(float originValueFloat) {
        this.originValueFloat = originValueFloat;
    }

    public float getOriginValueFloat1() {
        return originValueFloat1;
    }

    public void setOriginValueFloat1(float originValueFloat1) {
        this.originValueFloat1 = originValueFloat1;
    }

    public float getOriginValueFloat2() {
        return originValueFloat2;
    }

    public void setOriginValueFloat2(float originValueFloat2) {
        this.originValueFloat2 = originValueFloat2;
    }

    public int getOldValueInt() {
        return oldValueInt;
    }

    public void setOldValueInt(int oldValueInt) {
        this.oldValueInt = oldValueInt;
    }

    public float getOldValueFloat() {
        return oldValueFloat;
    }

    public void setOldValueFloat(float oldValueFloat) {
        this.oldValueFloat = oldValueFloat;
    }

    public float getOldValueFloat1() {
        return oldValueFloat1;
    }

    public void setOldValueFloat1(float oldValueFloat1) {
        this.oldValueFloat1 = oldValueFloat1;
    }

    public float getOldValueFloat2() {
        return oldValueFloat2;
    }

    public void setOldValueFloat2(float oldValueFloat2) {
        this.oldValueFloat2 = oldValueFloat2;
    }

    public int getNewValueInt() {
        return newValueInt;
    }

    public void setNewValueInt(int newValueInt) {
        this.newValueInt = newValueInt;
    }

    public float getNewValueFloat() {
        return newValueFloat;
    }

    public void setNewValueFloat(float newValueFloat) {
        this.newValueFloat = newValueFloat;
    }

    public float getNewValueFloat1() {
        return newValueFloat1;
    }

    public void setNewValueFloat1(float newValueFloat1) {
        this.newValueFloat1 = newValueFloat1;
    }

    public float getNewValueFloat2() {
        return newValueFloat2;
    }

    public void setNewValueFloat2(float newValueFloat2) {
        this.newValueFloat2 = newValueFloat2;
    }

    @Override
    public String toString() {
        return "EditOption{" +
                "type=" + type +
                ", changedLayer='" + changedLayer + '\'' +
                ", changedProperty='" + changedProperty + '\'' +
                ", originValueInt=" + originValueInt +
                ", originValueFloat=" + originValueFloat +
                ", originValueFloat1=" + originValueFloat1 +
                ", originValueFloat2=" + originValueFloat2 +
                ", oldValueInt=" + oldValueInt +
                ", oldValueFloat=" + oldValueFloat +
                ", oldValueFloat1=" + oldValueFloat1 +
                ", oldValueFloat2=" + oldValueFloat2 +
                ", newValueInt=" + newValueInt +
                ", newValueFloat=" + newValueFloat +
                ", newValueFloat1=" + newValueFloat1 +
                ", newValueFloat2=" + newValueFloat2 +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (!(obj instanceof EditOption)) return false;
        EditOption option = (EditOption) obj;

        if (option.getType() != this.getType()) return false;

        if (option.getType() == EditOption.SPEED || option.getType() == EditOption.BACK_GROUND || option.getType() == EditOption.TRIM) {
            return true;
        }
        if (option.getType() == EditOption.OPTION) {
            if (option.getChangedLayer() == this.changedLayer) {

                return option.getChangedProperty() == this.changedProperty;
            } else
                return false;
        } else {
            return false;

        }
    }

    public interface EditOptionListener {
        void onClose(EditOption temp);

        void onChangeColor(int color);

        void onReverse(boolean isReverse);

    }
}
