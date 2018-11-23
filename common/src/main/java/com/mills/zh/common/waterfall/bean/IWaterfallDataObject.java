package com.mills.zh.common.waterfall.bean;

import java.util.Iterator;

/**
 * @author zhangmd
 * @date 2017-5-9 -- 上午11:00:01
 */

public interface IWaterfallDataObject<WaterallRow> {
	int getRowCount();
	WaterallRow getRowData(int row);
	Iterator<WaterallRow> getRowIterator();
}
