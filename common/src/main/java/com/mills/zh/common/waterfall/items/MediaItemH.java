package com.mills.zh.common.waterfall.items;

import android.view.View;

import com.mills.zh.annotation.waterfall.WaterfallItem;
import com.mills.zh.common.R2;
import com.mills.zh.common.waterfall.BaseItem;

/**
 * Created by zhangmd on 2018/11/26.
 */

@WaterfallItem(
        type = "still",
        layout = R2.layout.waterfall_item_media_h_layout
)
public class MediaItemH extends BaseItem {

    public MediaItemH(View itemView, int itemType){
        super(itemView, itemType);
    }

    @Override
    public void initItemOffsets() {

    }
}
