/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mills.zh.common.waterfall.recyclerview;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;


/**
 * An extension to RecyclerView.Adapter to provide sections with headers and footers to a
 * RecyclerView. Each section can have an arbitrary number of items.
 * 
 * @modify by zhangmd 20170425
 *
 * @param <H> Class extending RecyclerView.ViewHolder to hold and bind the header view
 * @param <VH> Class extending RecyclerView.ViewHolder to hold and bind the items view
 * @param <F> Class extending RecyclerView.ViewHolder to hold and bind the footer view
 */
public abstract class SectionedRecyclerViewAdapter<H extends BaseViewHolder,
        VH extends BaseViewHolder,
        F extends BaseViewHolder>
        extends BaseRecyclerViewAdapter<BaseViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    protected static final int TYPE_SECTION_HEADER = -1;
    protected static final int TYPE_SECTION_FOOTER = -2;

    private int[] sectionForPosition = null;
    private int[] positionWithinSection = null;
    private int[] positionForSection = null;	// 每个section的起始position
    private boolean[] isHeader = null;
    private boolean[] isFooter = null;
    private int count = 0;

    public SectionedRecyclerViewAdapter() {
        super();
        registerAdapterDataObserver(new SectionDataObserver());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        setupIndices(false);
    }
    
    /**
     * Returns the sum of number of items for each section plus headers and footers if they
     * are provided.
     */
    @Override
    public int getItemCount() {
        return count;
    }

    public void setupIndices(boolean reset){
    	if(reset || sectionForPosition == null){
    		count = countItems();
            allocateAuxiliaryArrays(count);
            precomputeIndices();
    	}
    }

    private int countItems() {
        int count = 0;
        int sections = getSectionCount();

        for(int i = 0; i < sections; i++){
            count += (hasHeaderInSection(i) ? 1 : 0) + getItemCountForSection(i) + (hasFooterInSection(i) ? 1 : 0);
        }
        return count;
    }
    
    private void allocateAuxiliaryArrays(int count) {
        sectionForPosition = new int[count];
        positionWithinSection = new int[count];
        isHeader = new boolean[count];
        isFooter = new boolean[count];
    }

    private void precomputeIndices(){
        int sections = getSectionCount();
        int index = 0;

        positionForSection = new int[sections]; 
        
        for(int i = 0; i < sections; i++){
        	if(hasHeaderInSection(i)){
        		setPrecomputedItem(index, true, false, i, 0);
                index++;
        	}
        	
        	positionForSection[i] = index;

            for(int j = 0; j < getItemCountForSection(i); j++){
                setPrecomputedItem(index, false, false, i, j);
                index++;
            }

            if(hasFooterInSection(i)){
                setPrecomputedItem(index, false, true, i, 0);
                index++;
            }
        }
    }

    

    private void setPrecomputedItem(int index, boolean isHeader, boolean isFooter, int section, int position) {
        this.isHeader[index] = isHeader;
        this.isFooter[index] = isFooter;
        sectionForPosition[index] = section;
        positionWithinSection[index] = position;
    }

    
    @Override
    public BaseViewHolder doCreateViewHolder(ViewGroup parent, int viewType) {
    	BaseViewHolder viewHolder;
        Log.d(TAG, "onCreateViewHolder viewType:"+viewType);
        if(isSectionHeaderViewType(viewType)){
            viewHolder = onCreateSectionHeaderViewHolder(parent, viewType);
        }else if(isSectionFooterViewType(viewType)){
            viewHolder = onCreateSectionFooterViewHolder(parent, viewType);
        }else{
            viewHolder = onCreateItemViewHolder(parent, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
    	int section = getSectionForPosition(position);
        int index = getPositionWithInSection(position);

        if(isSectionHeaderPosition(position)){
            onBindSectionHeaderViewHolder((H) holder, section);
        }else if(isSectionFooterPosition(position)){
            onBindSectionFooterViewHolder((F) holder, section);
        }else{
            onBindItemViewHolder((VH) holder, section, index);
        }

    }

    @Override
    public int getItemViewType(int position) {

        setupIndices(false);

        int section = getSectionForPosition(position);
        int index = getPositionWithInSection(position);

        if(isSectionHeaderPosition(position)){
            return getSectionHeaderViewType(section);
        }else if(isSectionFooterPosition(position)){
            return getSectionFooterViewType(section);
        }else{
            return getSectionItemViewType(section, index);
        }

    }
    
    public int getSectionForPosition(int position){
        if(sectionForPosition == null || sectionForPosition.length == 0) {
            return 0;
        }
    	return sectionForPosition[position];
    }
    
    public int getPositionWithInSection(int position){
        if(positionWithinSection == null || positionWithinSection.length == 0) {
            return 0;
        }
    	return positionWithinSection[position];
    }
    
    public int getPositionForSection(int section){
        if(positionForSection == null || positionForSection.length == 0) {
            return 0;
        }
    	return positionForSection[section];
    }

    protected int getSectionHeaderViewType(int section){
        return TYPE_SECTION_HEADER;
    }

    protected int getSectionFooterViewType(int section){
        return TYPE_SECTION_FOOTER;
    }

    /**
     * Returns true if the argument position corresponds to a header
     */
    public boolean isSectionHeaderPosition(int position){
        setupIndices(false);
        return isHeader[position];
    }

    /**
     * Returns true if the argument position corresponds to a footer
     */
    public boolean isSectionFooterPosition(int position){
        setupIndices(false);
        return isFooter[position];
    }

    protected boolean isSectionHeaderViewType(int viewType){
        return viewType == TYPE_SECTION_HEADER;
    }

    protected boolean isSectionFooterViewType(int viewType){
        return viewType == TYPE_SECTION_FOOTER;
    }

    /**
     * Returns the number of sections in the RecyclerView
     */
    public abstract int getSectionCount();

    /**
     * Returns the number of items for a given section
     */
    protected abstract int getItemCountForSection(int section);
    
    /**
     * Returns the data of items for a given section
     */
    public abstract Object getDataForSection(int section);

    /**
     * Returns true if a given section should have a header
     */
    protected abstract boolean hasHeaderInSection(int section);
    
    /**
     * Returns true if a given section should have a footer
     */
    protected abstract boolean hasFooterInSection(int section);

    /**
     * Creates a ViewHolder of class H for a Header
     */
    protected abstract H  onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType);

    /**
     * Creates a ViewHolder of class F for a Footer
     */
    protected abstract F  onCreateSectionFooterViewHolder(ViewGroup parent, int viewType);

    /**
     * Creates a ViewHolder of class VH for an Item
     */
    protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    /**
     * Binds data to the header view of a given section
     */
    protected abstract void onBindSectionHeaderViewHolder(H holder, int section);

    /**
     * Binds data to the footer view of a given section
     */
    protected abstract void onBindSectionFooterViewHolder(F holder, int section);

    /**
     * Binds data to the item view for a given position within a section
     */
    protected abstract void onBindItemViewHolder(VH holder, int section, int position);
    
    /**
     * Returns item view type for a given position within section
     */
    protected abstract int getSectionItemViewType(int section, int position);
    
    /**
     * Returns item span size for a given position within section
     */
    protected abstract int getSectionItemSpanSize(int section, int position);

    class SectionDataObserver extends RecyclerView.AdapterDataObserver{
        @Override
        public void onChanged() {
            setupIndices(true);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            setupIndices(true);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            setupIndices(true);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            setupIndices(true);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            setupIndices(true);
        }
    }
    
    public static class SectionedSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        protected SectionedRecyclerViewAdapter<?, ?, ?> adapter = null;
        protected GridLayoutManager layoutManager = null;

        public SectionedSpanSizeLookup(SectionedRecyclerViewAdapter<?, ?, ?> adapter, GridLayoutManager layoutManager) {
            this.adapter = adapter;
            this.layoutManager = layoutManager;
        }

        @Override
        public int getSpanSize(int position) {

            if(adapter.isSectionHeaderPosition(position) || adapter.isSectionFooterPosition(position)){
                return layoutManager.getSpanCount();
            }else{
                return adapter.getSectionItemSpanSize(adapter.getSectionForPosition(position), adapter.getPositionWithInSection(position));
            }

        }
    }
}
