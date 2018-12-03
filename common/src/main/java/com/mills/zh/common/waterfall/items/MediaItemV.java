package com.mills.zh.common.waterfall.items;

import android.view.View;

import com.mills.zh.annotation.waterfall.WaterfallItem;
import com.mills.zh.common.R2;
import com.mills.zh.common.waterfall.BaseItem;

/**
 * Created by zhangmd on 2018/11/23.
 */

@WaterfallItem(
        type = {"media_v", "media_list_v"},
        layout = R2.layout.waterfall_item_media_v_layout
)
public class MediaItemV extends BaseItem {

    public MediaItemV(View itemView, int itemType){
        super(itemView, itemType);
    }

    @Override
    public void initItemOffsets() {

    }
}
