package com.mills.zh.compiler.utils;

/**
 * Created by zhangmd on 2018/11/20.
 */

public interface Constants {

    String ANNOTATION_PKG = "com.mills.zh.annotation";

    String ANNOTATION_TYPE_WATERFALL= ANNOTATION_PKG + ".Waterfall";
    String ANNOTATION_TYPE_WATERFALL_ITEM = ANNOTATION_PKG + ".WaterfallItem";


    String WATERFALL_PKG = "com.mills.zh.common.waterfall";

    String WATERFALL_CLASS = "Waterfall";
    String WATERFALL_TEMPLATE_PREFIX = "TEMPLATE_";
    String WATERFALL_TEMPLATES = "Templates";

    String WATERFALL_ITEM_FACTORY_CLASS = "WaterfallItemFactory";
    int WATERFALL_ITEM_TYPE_START_INDEX = 1000;
    String WATERFALL_ITEM_TYPE_PREFIX = "ITEM_TYPE_";

}
