package com.mills.zh.waterfall.items;

import android.view.View;

import com.mills.zh.R;
import com.mills.zh.annotation.WaterfallItem;
import com.mills.zh.common.waterfall.BaseItem;

/**
 * Created by zhangmd on 2018/11/23.
 */

@WaterfallItem(type = "media_v", layout = R.layout.waterfall_item_media_v_layout)
public class MediaItemV extends BaseItem {


    public MediaItemV(View itemView, int itemType){
        super(itemView, itemType);
    }

    @Override
    public void initItemOffsets() {

    }
}
