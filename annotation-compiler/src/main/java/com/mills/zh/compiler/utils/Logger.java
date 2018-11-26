package com.mills.zh.compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

/**
 * Created by zhangmd on 2018/11/21.
 */

public class Logger {
    private Messager messager;

    public Logger(Messager messager){
        this.messager = messager;
    }

    public void info(String tag, CharSequence info){
        messager.printMessage(Kind.NOTE, "[" + tag + "]" + info);
    }

    public void error(String tag, CharSequence error){
        messager.printMessage(Kind.ERROR, "[" + tag + "]" + error);
    }

    public void error(String tag, Throwable error) {
        if (null != error) {
            messager.printMessage(Diagnostic.Kind.ERROR, "[" + tag + "]" + error.getMessage() + "\n" + formatStackTrace(error.getStackTrace()));
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}