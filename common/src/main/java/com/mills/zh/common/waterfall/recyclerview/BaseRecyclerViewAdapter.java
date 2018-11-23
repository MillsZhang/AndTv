package com.mills.zh.common.waterfall.recyclerview;


import android.support.v7.widget.RecyclerView.Adapter;
import android.view.ViewGroup;

import com.mills.zh.common.waterfall.recyclerview.BaseViewHolder.OnFocusChangeListener;
import com.mills.zh.common.waterfall.recyclerview.BaseViewHolder.OnItemClickListener;
import com.mills.zh.common.waterfall.recyclerview.BaseViewHolder.OnItemSelectedListener;


/**
 * @author zhangmd
 * @date 2017-5-16 -- 下午6:23:18
 */

public abstract class BaseRecyclerViewAdapter<VH extends BaseViewHolder> extends Adapter<VH> {

	private OnItemClickListener mOnItemClickListener;
	private OnItemSelectedListener mOnItemSelectedListener;
	private OnFocusChangeListener mOnFocusChangeListener;
	
	public void setOnItemClickListener(OnItemClickListener listener){
		mOnItemClickListener = listener;
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelectedListener = listener;
	}
	
	public void setOnFocusChangeListener(OnFocusChangeListener listener){
		mOnFocusChangeListener = listener;
	}
	
	public abstract VH doCreateViewHolder(ViewGroup parent, int viewType);
	
	@Override
	final public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		VH viewHolder = doCreateViewHolder(parent, viewType);
		if(viewHolder != null){
			viewHolder.setOnItemClickListener(mOnItemClickListener);
			viewHolder.setOnItemSelectedListener(mOnItemSelectedListener);
			viewHolder.setOnFocusChangeListener(mOnFocusChangeListener);
		}
		return viewHolder;
	}
}
