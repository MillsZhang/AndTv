package com.mills.zh.annotarion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhangmd on 2018/11/20.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Waterfall {

    String template() default "";
}