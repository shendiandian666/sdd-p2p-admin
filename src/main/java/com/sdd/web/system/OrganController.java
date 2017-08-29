package com.sdd.web.system;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageInfo;
import com.sdd.model.Page;
import com.sdd.model.PageData;
import com.sdd.service.system.OrganService;
import com.sdd.util.Tools;
import com.sdd.web.WebController;

@Controller
public class OrganController extends WebController {

	@Autowired
	private OrganService organService;
	
	/*
	 * 查询列表
	 */
	@RequestMapping("/organList")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = organService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("organList", list);
		map.put("pd", pd);
		return "/system/organ/organ_list";
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("/organSave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception{
		return organService.saveService(getPageData());
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("/organUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		return organService.updateService(pd);
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("/organDel")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception{
		//organService.delService(getPageData());
		//map.put("msg", Constants.SUCCESS);
		return organService.delService(getPageData());
	}
	
	/*
	 * 去添加页面
	 */
	@RequestMapping("/organAdd")
	public String add(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		List<Map<String, Object>> organDicList = organService.organDic(pd);
		map.put("organDicList", organDicList);
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/system/organ/organ_edit";
	}
	
	/*
	 * 去修改页面
	 */
	@RequestMapping("/organEdit")
	public String edit(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		Map<String, Object> result = organService.editService(pd);
		List<Map<String, Object>> organDicList = organService.organDic(pd);
		map.put("organDicList", organDicList);
		map.put("organ", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/system/organ/organ_edit";
	}
	
}
