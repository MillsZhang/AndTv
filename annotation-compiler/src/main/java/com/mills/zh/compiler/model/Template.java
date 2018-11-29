package com.mills.zh.compiler.model;

import javax.lang.model.type.TypeMirror;

/**
 * Created by zhangmd on 2018/11/22.
 */

public class Template {
    private String template;
    private TypeMirror typeMirror;

    public Template(){

    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public void setTypeMirror(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }
}
