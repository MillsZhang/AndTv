package com.mills.zh.compiler.utils;

/**
 * Created by zhangmd on 2018/11/20.
 */

public interface Constants {

    String ANNOTATION_PKG = "com.mills.zh.annotation";

    String ANNOTATION_TYPE_WATERFALL_TEMPLATE = ANNOTATION_PKG + ".WaterfallTemplate";
    String ANNOTATION_TYPE_WATERFALL_ITEM = ANNOTATION_PKG + ".WaterfallItem";

    String WATERFALL_COMMON_PKG = "com.mills.zh.common.waterfall";
    String WATERFALL_BASE_ITEM_CLASS = "BaseItem";
    String WATERFALL_BASE_TEMPLATE_CLASS = "Template";

    String ANNOTATION_GEN_PKG = "com.mills.zh.gen";
    String WATERFALL_PKG = ANNOTATION_GEN_PKG + ".waterfall";
    String WATERFALL_TEMPLATE_CLASS = "WaterfallTemplates";
    String WATERFALL_TEMPLATE_PREFIX = "TEMPLATE_";
    String WATERFALL_TEMPLATES = "Templates";
    String WATERFALL_METHOD_GET_TEMPLATE = "getTemplate";

    String WATERFALL_ITEM_CLASS = "WaterfallItems";
    int WATERFALL_ITEM_TYPE_START_INDEX = 1000;
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
