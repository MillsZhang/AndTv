package com.mills.zh.common.waterfall.templates;

import com.mills.zh.annotation.waterfall.WaterfallTemplate;
import com.mills.zh.common.waterfall.Template.FlexibleTemplate;

/**
 * Created by zhangmd on 2018/11/23.
 */

@WaterfallTemplate(type = {"poster"})
public class PosterTemplate extends FlexibleTemplate {
    public PosterTemplate(){
        super(12);
    }

    @Override
    public int getItemSpanSize(int position) {
        return 2;
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
