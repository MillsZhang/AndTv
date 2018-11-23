package com.mills.zh.common.waterfall;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.mills.zh.common.waterfall.recyclerview.BaseViewHolder;


/**
 * @author zhangmd
 * @date 2017-5-8 -- 上午11:31:22
 */

public abstract class BaseItem extends BaseViewHolder {

	// 卡片图片展示类型
	public static final int IMAGE_TYPE_NORMAL = 0;
	public static final int IMAGE_TYPE_MULTILAYER = 1;
	public static final int IMAGE_TYPE_GIF = 2;

	protected int mItemType;
	protected View mItemView;

	protected Activity mAttachedActivity;
	protected Fragment mAttachedFragment;
	
	protected Object mBindData;
	private String mFloorTitle;
	private int mPositionInFloor;
	private boolean mScrollItem;
	private int mSectionPosition;
	private int mItemCountInSection;
	
	protected Rect mItemOffsets = new Rect();
	private boolean mUseSpecialItemOffsets = false;
	
	private boolean mHasImageRecycled = false;
	
	private String mTemplate;

	public BaseItem(View itemView, int itemType) {
		super(itemView);
		itemView.setTag(this);
		mItemView = itemView;
		mItemType = itemType;
		initItemOffsets();
	}

	public void setAttachedActivity(Activity activity){
		mAttachedActivity = activity;
	}

	public void setAttachedFragment(Fragment fragment){
		mAttachedFragment = fragment;
	}

	public Activity getAttachedActivity(){
		return mAttachedActivity;
	}

	public Fragment getAttachedFragment(){
		return mAttachedFragment;
	}

	public Object getActtachedContext(){
		if(mAttachedFragment != null){
			return mAttachedFragment;
		} else if(mAttachedActivity != null){
			return mAttachedActivity;
		}
		return null;
	}

	public void setSize(int width, int height) {
		if (mItemView != null) {
			LayoutParams params = mItemView.getLayoutParams();
			params.width = width;
			params.height = height;
			mItemView.setLayoutParams(params);
		}
	}
	
	public View getItemView(){
		return mItemView;
	}

	public void setItemType(int type){
		mItemType = type;
	}
	
	public int getItemType(){
		return mItemType;
	}
	
	public Rect getItemOffsets(){
		return mItemOffsets;
	}
	
	public boolean isUseSpecialItemOffsets() {
		return mUseSpecialItemOffsets;
	}

	public void setUseSpecialItemOffsets(boolean useSpecialItemOffsets) {
		this.mUseSpecialItemOffsets = useSpecialItemOffsets;
	}
	
	public abstract void initItemOffsets();
	
	public boolean bindData(Object data){
		if(mBindData != null && mBindData == data){
			// RecyclerView从Recyler中复用item时，如果数据不变就不会重新绑定数据，如果该item的image被释放了，会导致不会重新拉取图片
			if(mHasImageRecycled){
				checkImage();
			}
			return false;
		}
		mBindData = data;
		return true;
	}
	
	public void checkImage(){
		mHasImageRecycled = false;
	}
	
	public void recycleImage(){
		mHasImageRecycled = true;
	}
	
	public Object getBindData(){
		return mBindData;
	}
	
	public void setFloorTitle(String title){
		mFloorTitle = title;
	}
	
	public String getFloorTitle(){
		return mFloorTitle;
	}

	public boolean isScrollItem() {
		return mScrollItem;
	}

	public void setScrollItem(boolean scrollItem) {
		mScrollItem = scrollItem;
	}	

	public int getSectionPosition() {
		return mSectionPosition;
	}

	public void setSectionPosition(int position) {
		mSectionPosition = position;
	}

	public int getItemCountInSection() {
		return mItemCountInSection;
	}

	public void setItemCountInSection(int count) {
		mItemCountInSection = count;
	}

	public String getTemplate() {
		return mTemplate;
	}

	public void setTemplate(String template) {
		this.mTemplate = template;
	}

	public int getPositionInFloor() {
		return mPositionInFloor;
	}

	public void setPositionInFloor(int mPositionInFloor) {
		this.mPositionInFloor = mPositionInFloor;
	}
}
