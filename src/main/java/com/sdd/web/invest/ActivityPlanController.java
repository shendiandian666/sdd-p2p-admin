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
import com.sdd.service.invest.ActivityPlanService;
import com.sdd.service.invest.ActivityService;
import com.sdd.service.invest.PlatformService;
import com.sdd.util.Tools;
import com.sdd.web.WebController;
/** 
 * 说明：
 * 创建人：kbky
 * 创建时间：
 */
@Controller("investActivityPlanController")
@RequestMapping(value="/invest")
public class ActivityPlanController extends WebController {
	
	@Autowired
	private ActivityService activityService;
	@Autowired
	private ActivityPlanService investActivityPlanService;
	@Autowired
	private PlatformService platformService;
	
	/*
	 * 查询
	 */
	@RequestMapping("/activityplanList")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = investActivityPlanService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("activityplanList", list);
		map.put("pd", pd);
		map.put("platformList", platformService.listDicService());
		return "/invest/activityplan/activityplan_list";
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("/activityplanSave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception{
		return investActivityPlanService.saveService(getPageData());
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("/activityplanUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception{
		return investActivityPlanService.updateService(getPageData());
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("/activityplanDel")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception{
		return investActivityPlanService.delService(getPageData());
	}
	
	/*
	 * 跳转到新增页面
	 */
	@RequestMapping("/activityplanAdd")
	public String add(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		
		map.put("activityList", activityService.listDicService());
		
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/activityplan/activityplan_edit";
	}
	
	/*
	 * 跳转到修改页面
	 */
	@RequestMapping("/activityplanEdit")
	public String edit(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		map.put("activityList", activityService.listDicService());
		
		Map<String, Object> result = investActivityPlanService.editService(pd);
		map.put("activityplan", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/activityplan/activityplan_edit";
	}
	
	/*
	 * 跳转到详情页面
	 */
	@RequestMapping("/activityplanDetail")
	public String detail(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		Map<String, Object> result = investActivityPlanService.editService(pd);
		map.put("activityplan", result);
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/activityplan/activityplan_detail";
	}
}
