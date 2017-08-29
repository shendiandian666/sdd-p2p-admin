package com.sdd.web.invest;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageInfo;
import com.sdd.model.Page;
import com.sdd.model.PageData;
import com.sdd.service.invest.ActivityService;
import com.sdd.service.invest.PlatformService;
import com.sdd.service.system.DictionariesService;
import com.sdd.util.Tools;
import com.sdd.web.WebController;
/** 
 * 说明：
 * 创建人：kbky
 * 创建时间：
 */
@Controller("investActivityController")
@RequestMapping(value="/invest")
public class ActivityController extends WebController {
	
	@Autowired
	private ActivityService investActivityService;
	@Autowired
	private DictionariesService dictionariesService;
	@Autowired
	private PlatformService platformService;
	
	/*
	 * 查询
	 */
	@RequestMapping("/activityList")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = investActivityService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("activityList", list);
		map.put("pd", pd);
		map.put("platformList", platformService.listDicService());
		return "/invest/activity/activity_list";
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("/activitySave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception{
		return investActivityService.saveService(getPageData());
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("/activityUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception{
		return investActivityService.updateService(getPageData());
	}
	
	/**
	 * 重置最佳方案
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/activityBest")
	@ResponseBody
	public String best(Map<String, Object> map) throws Exception{
		return investActivityService.bestService(getPageData());
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("/activityDel")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception{
		return investActivityService.delService(getPageData());
	}
	
	/*
	 * 跳转到新增页面
	 */
	@RequestMapping("/activityAdd")
	public String add(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		
		map.put("platformList", platformService.listDicService());
		
		pd.put("TABLE_NAME", "p2p_activity");
		pd.put("TABLE_COLUMN", "isFirst");
		map.put("isfirstList", dictionariesService.listDicService(pd));
		
		pd.put("TABLE_NAME", "p2p_activity");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/activity/activity_edit";
	}
	
	/*
	 * 跳转到修改页面
	 */
	@RequestMapping("/activityEdit")
	public String edit(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		
		map.put("platformList", platformService.listDicService());
		
		pd.put("TABLE_NAME", "p2p_activity");
		pd.put("TABLE_COLUMN", "isFirst");
		map.put("isfirstList", dictionariesService.listDicService(pd));
		
		pd.put("TABLE_NAME", "p2p_activity");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		Map<String, Object> result = investActivityService.editService(pd);
		map.put("activity", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/activity/activity_edit";
	}
	
	/*
	 * 跳转到详情页面
	 */
	@RequestMapping("/activityDetail")
	public String detail(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		Map<String, Object> result = investActivityService.editService(pd);
		map.put("activity", result);
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/activity/activity_detail";
	}
}
