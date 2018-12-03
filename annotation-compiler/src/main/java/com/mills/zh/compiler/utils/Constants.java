package com.mills.zh.compiler.utils;

/**
 * Created by zhangmd on 2018/11/20.
 */

public interface Constants {

    String ANNOTATION_PKG = "com.mills.zh.annotation";

    String ANNOTATION_WATERFALL = ".waterfall";
    String ANNOTATION_TYPE_WATERFALL_TEMPLATE = ANNOTATION_PKG + ANNOTATION_WATERFALL + ".WaterfallTemplate";
    String ANNOTATION_TYPE_WATERFALL_ITEM = ANNOTATION_PKG + ANNOTATION_WATERFALL + ".WaterfallItem";

    String WATERFALL_COMMON_PKG = "com.mills.zh.common.waterfall";
    String WATERFALL_BASE_ITEM_CLASS = "BaseItem";
    String WATERFALL_BASE_TEMPLATE_CLASS = "Template";

    String ANNOTATION_GEN_PKG = "com.mills.zh.gen";
    String ANNOTATION_GEN_WATERFALL = ".waterfall";
    String WATERFALL_TEMPLATE_CLASS = "WaterfallTemplates";
    String WATERFALL_TEMPLATE_PREFIX = "TEMPLATE_";
    String WATERFALL_TEMPLATES = "Templates";
    String WATERFALL_METHOD_GET_TEMPLATE = "getTemplate";

    String WATERFALL_ITEM_CLASS = "WaterfallItems";
    int WATERFALL_ITEM_TYPE_DEFAULT_START_ID = 10000;
    String WATERFALL_ITEM_TYPE_PREFIX = "ITEM_TYPE_";
    String WATERFALL_ITEMS_METHOD_GET_ITEM = "getItem";

    String ANDROID_APP_PKG = "android.app";
    String ANDROID_SUPPORT_V4_APP_PKG = "android.support.v4.app";
    String ACTIVITY = "Activity";
    String FRAGMENT = "Fragment";

    String ANDROID_VIEW_PKG = "android.view";
    String VIEW = "View";
    String VIEWGROUP = "ViewGroup";
    String LAYOUT_INFLATER = "LayoutInflater";

}
