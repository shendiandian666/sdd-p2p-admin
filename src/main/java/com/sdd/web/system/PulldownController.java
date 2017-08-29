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
import com.sdd.service.system.PulldownService;
import com.sdd.util.Constants;
import com.sdd.util.JsonTool;
import com.sdd.web.WebController;

@Controller
public class PulldownController extends WebController {

	@Autowired
	private PulldownService pulldownService;
	
	/*
	 * 查询列表
	 */
	@RequestMapping("/pulldownList")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = pulldownService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("pulldownList", list);
		map.put("pd", pd);
		return "/system/pulldown/pulldown_list";
	}
	
	/*
	 * 获取字典树形菜单
	 */
	@RequestMapping("/pulldownSelectDic")
	public String selectDic(Map<String, Object> map)throws Exception{
		map.put("ID", getPageData().get("ID"));
		List<Map<String, Object>> list = pulldownService.getTreeList(getRequest(), getPageData());
		String json = JsonTool.beanToJson(list);
		json = json.replaceAll("DICTIONARIES_ID", "id").replaceAll("PARENT_ID", "pId")
				.replaceAll("NAME", "name").replaceAll("children", "nodes");
		map.put("zTreeNodes", json);
		return "/system/pulldown/dic_tree";
	}
	
	/*
	 * 修改下拉列表对应的数据字典
	 */
	@RequestMapping("/pulldownUpdateDic")
	@ResponseBody
	public boolean updateDic(Map<String, Object> map) throws Exception{
		pulldownService.updateDic(getPageData());
		return true;
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("/pulldownSave")
	public String save(Map<String, Object> map) throws Exception{
		pulldownService.saveService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("/pulldownUpdate")
	public String update(Map<String, Object> map) throws Exception{
		pulldownService.updateService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("/pulldownDel")
	public String del(Map<String, Object> map) throws Exception{
		pulldownService.delService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	/*
	 * 去添加页面
	 */
	@RequestMapping("/pulldownAdd")
	public String add(Map<String, Object> map) throws Exception{
		map.put("action", "Save");
		return "/system/pulldown/pulldown_edit";
	}
	
	/*
	 * 去修改页面
	 */
	@RequestMapping("/pulldownEdit")
	public String edit(Map<String, Object> map) throws Exception{
		Map<String, Object> result = pulldownService.editService(getPageData());
		map.put("pulldown", result);
		map.put("action", "Update");
		return "/system/pulldown/pulldown_edit";
	}
	
}
