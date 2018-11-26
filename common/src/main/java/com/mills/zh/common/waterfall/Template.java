package com.mills.zh.common.waterfall;

import com.mills.zh.common.waterfall.bean.IWaterfallRowObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangmd on 2018/11/22.
 */

public abstract class Template {
    protected int mItemCount;
    protected boolean mFlexible = false;

    public Template(int count){
        mItemCount = count;
    }

    public void setItemCount(int count) {
        this.mItemCount = count;
    }

    public int getItemCount(){
        return mItemCount;
    }

    public boolean isFlexible(){
        return mFlexible;
    }

    public abstract int getItemType(int position);

    public abstract int getItemSpanSize(int position);

    public int getDataIndexByPositon(int position){
        return position;
    }

    public <Media> boolean fillData(List<Media> datas, Class<Media> mediaClass) throws InstantiationException, IllegalAccessException{
        if (datas != null && datas.size() >= mItemCount) {
            return true;
        }

        if (datas == null) {
            datas = new ArrayList<Media>(mItemCount);
        }
        int size = datas.size();
        for (int i = mItemCount - size; i > 0; i--) {
            datas.add(mediaClass.newInstance());
        }
        return false;
    }

    public void handleSpecialItemOffsets(BaseItem holder, int position){
        holder.setUseSpecialItemOffsets(false);
    }

    public <Row extends IWaterfallRowObject<? extends Object>> void bindItemData(BaseItem holder, int position, Row row){
        int index = getDataIndexByPositon(position);
        if(index >= 0 && index < row.getRowData().size()){
            holder.bindData(row.getRowData().get(index));
        }
    }


    public static abstract class FlexibleTemplate extends Template {

        protected int mActualItemCount;

        public FlexibleTemplate(int maxCount){
            super(maxCount);
            mFlexible = true;
        }

        @Override
        public void setItemCount(int count) {
            mActualItemCount = count;
        }

        @Override
        public int getItemCount() {
            return mActualItemCount > mItemCount ? mItemCount : mActualItemCount;
        }

        public void setActualItemCount(int count){
            mActualItemCount = count;
        }

        public int getMaxItemCount(){
            return mItemCount;
        }

        @Override
        public <Media> boolean fillData(List<Media> datas, Class<Media> mediaClass) throws InstantiationException, IllegalAccessException {
            return true;
        }
    }
}
