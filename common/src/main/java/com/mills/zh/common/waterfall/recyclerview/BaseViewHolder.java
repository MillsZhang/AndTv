package com.mills.zh.common.waterfall.recyclerview;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

/**
 * @author zhangmd
 * @date 2017-5-16 -- 下午6:30:24
 */

public abstract class BaseViewHolder extends ViewHolder implements OnFocusChangeListener, OnClickListener {
	
	private OnItemClickListener mOnItemClickListener;
	private OnItemSelectedListener mOnItemSelectedListener;
	private OnFocusChangeListener mOnFocusChangeListener;

	public BaseViewHolder(View itemView) {
		super(itemView);
		if(itemView != null){
			if(itemView.isFocusable()){
				itemView.setOnFocusChangeListener(this);
				itemView.setOnClickListener(this);
			}
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		mOnItemClickListener = listener;
	}
	
	public OnItemClickListener getItemClickListener(){
		return mOnItemClickListener;
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelectedListener = listener;
	}
	
	public void setOnFocusChangeListener(OnFocusChangeListener listener){
		mOnFocusChangeListener = listener;
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(mOnFocusChangeListener != null){
			mOnFocusChangeListener.onFocusChange(v, this, hasFocus);
		}
		if(hasFocus && mOnItemSelectedListener != null){
			int position = getAdapterPosition();
			if(position >= 0){
				mOnItemSelectedListener.onItemSelected(v, this, position);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(mOnItemClickListener != null){
			int position = getAdapterPosition();
			if(position >= 0){
				mOnItemClickListener.onItemClick(v, this, position);
			}
		}
	}
	
	public interface OnItemClickListener {
		void onItemClick(View view, BaseViewHolder viewHolder, int position);
	}
	
	public interface OnItemSelectedListener {
		void onItemSelected(View view, BaseViewHolder viewHolder, int position);
	}
	
	public interface OnFocusChangeListener {
		void onFocusChange(View view, BaseViewHolder viewHolder, boolean hasFocus);
	}
}
