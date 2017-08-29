package com.sdd.web.system;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageInfo;
import com.sdd.model.Page;
import com.sdd.model.PageData;
import com.sdd.service.system.DictionariesService;
import com.sdd.util.Constants;
import com.sdd.util.JsonTool;
import com.sdd.web.WebController;

@Controller
public class DictionariesController extends WebController {

	@Autowired
	private DictionariesService dictionariesService;
	
	/*
	 * 页面入口
	 */
	@RequestMapping("/dictionariesList")
	public String permissionList(Map<String, Object> map) throws Exception {
		List<Map<String, Object>> list = dictionariesService.getRootList(getRequest());
		String json = JsonTool.beanToJson(list);
		json = json.replaceAll("B_ID", "id").replaceAll("B_PID", "pid")
				.replaceAll("B_NAME", "name").replaceAll("B_URL", "url");
		map.put("zTreeNodes", json);
		return "/system/dictionaries/dictionaries_ztree";
	}
	
	/*
	 * 左侧树形菜单
	 */
	@RequestMapping("/dictionariesNodes")
	@ResponseBody
	public String dictionariesNodes(Map<String, Object> map) throws Exception {
		List<Map<String, Object>> list = dictionariesService.getNodes(getRequest(),getPageData());
		String json = JsonTool.beanToJson(list);
		json = json.replaceAll("B_ID", "id").replaceAll("B_PID", "pid")
				.replaceAll("B_NAME", "name").replaceAll("B_URL", "url");
		return json;
	}
	
	/*
	 * 右侧查询列表
	 */
	@RequestMapping("/dictionariesRight")
	public String permissionRight(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = dictionariesService.dictionariesRight(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("rightList", list);
		map.put("TREE_ID", MapUtils.getString(pd, "TREE_ID"));
		return "/system/dictionaries/dictionaries_list";
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("dictionariesSave")
	public String save(Map<String, Object> map) throws Exception {
		dictionariesService.saveService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("dictionariesUpdate")
	public String update(Map<String, Object> map) throws Exception {
		dictionariesService.updateService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("dictionariesDel")
	@ResponseBody
	public boolean del(Map<String, Object> map) throws Exception {
		int count = dictionariesService.delService(getPageData());
		if(count > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * 去添加页面
	 */
	@RequestMapping("dictionariesAdd")
	public String add(Map<String, Object> map) throws Exception {
		map.put("TREE_ID", MapUtils.getString(getPageData(), "TREE_ID"));
		Map<String, Object> parent = dictionariesService.findOneService(getPageData());
		map.put("parent", parent);
		map.put("action", "Save");
		return "/system/dictionaries/dictionaries_edit";
	}
	
	/*
	 * 去修改页面
	 */
	@RequestMapping("dictionariesEdit")
	public String edit(Map<String, Object> map) throws Exception {
		map.put("TREE_ID", MapUtils.getString(getPageData(), "TREE_ID"));
		Map<String, Object> result = dictionariesService.findOneService(getPageData());
		map.put("result", result);
		map.put("action", "Update");
		return "/system/dictionaries/dictionaries_edit";
	}
	
	
	
}
