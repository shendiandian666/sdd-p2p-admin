package com.sdd.model;

import com.sdd.util.Tools;

public class Page {

	public static int getPageNum(PageData pd){
		int num = Tools.getIntValue(pd, "pageNum");
		if(num == 0){
			return 1;
		}else{
			return num;
		}
	}
	
	public static int getPageSize(PageData pd){
		int size = Tools.getIntValue(pd, "pageSize");
		if(size == 0){
			return 10;
		}else{
			return size;
		}
	}
	
}
