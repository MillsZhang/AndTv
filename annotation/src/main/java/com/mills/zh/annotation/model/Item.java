package com.mills.zh.annotation.model;

import javax.lang.model.type.TypeMirror;

/**
 * Created by zhangmd on 2018/11/22.
 */

public class Item {
    private String typeName;
    private int type;
    private int layout;
    private TypeMirror typeMirror;

    public Item(){

    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public void setTypeMirror(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }
}
