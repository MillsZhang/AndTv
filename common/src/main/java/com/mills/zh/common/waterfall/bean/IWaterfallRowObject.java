package com.mills.zh.common.waterfall.bean;

import java.util.List;

/**
 * @author zhangmd
 * @date 2017-12-8 -- 下午5:59:51
 *   从WaterfallRow,DetailWaterfullRow,LiveWaterfallRow等瀑布流楼层数据结构
 *   中抽取统一的接口
 */

public interface IWaterfallRowObject<WaterfallItemData extends Object> {

	public String getTemplateStyle();
	public List<WaterfallItemData> getRowData();
	public int getRowDataSize();
	public String getRowTitle();
	public String getRowName();

}
