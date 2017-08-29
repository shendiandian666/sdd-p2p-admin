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
import com.sdd.service.system.PermissionService;
import com.sdd.util.JsonTool;
import com.sdd.util.Tools;
import com.sdd.web.WebController;

@Controller
public class PermissionController extends WebController {

	@Autowired
	private PermissionService permissionService;
	@Autowired
	private DictionariesService dictionariesService;
	
	/*
	 * 页面入口
	 */
	@RequestMapping("/permissionList")
	public String permissionList(Map<String, Object> map) throws Exception {
		List<Map<String, Object>> list = permissionService.getRootList(getRequest());
		String json = JsonTool.beanToJson(list);
		json = json.replaceAll("B_ID", "id").replaceAll("B_PID", "pid")
				.replaceAll("B_NAME", "name").replaceAll("B_URL", "url");
		map.put("zTreeNodes", json);
		return "/system/permission/permission_ztree";
	}
	
	/*
	 * 获取树形菜单节点
	 */
	@RequestMapping("/permissionNodes")
	@ResponseBody
	public String permissionNodes(Map<String, Object> map) throws Exception {
		List<Map<String, Object>> list = permissionService.getNodes(getRequest(), getPageData());
		String json = JsonTool.beanToJson(list);
		json = json.replaceAll("B_ID", "id").replaceAll("B_PID", "pid")
				.replaceAll("B_NAME", "name").replaceAll("B_URL", "url");
		return json;
	}
	
	/*
	 * 获取右侧查询列表
	 */
	@RequestMapping("/permissionRight")
	public String permissionRight(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = permissionService.permissionRight(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("rightList", list);
		map.put("PARENT_ID", MapUtils.getString(pd, "PARENT_ID"));
		return "/system/permission/permission_list";
	}
	
	/*
	 * 菜单图标修改
	 */
	@RequestMapping("/permissionEditTb")
	public String editTb(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		String menuId = Tools.getStringValue(pd.get("TREE_ID"));
		map.put("TREE_ID", menuId);
		map.put("PARENT_ID", Tools.getStringValue(pd.get("PARENT_ID")));
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/system/permission/menu_icon";
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("/permissionSave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception {
		return permissionService.saveService(getPageData());
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("/permissionUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception {
		return permissionService.updateService(getPageData());
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("/permissionDel")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception {
		return permissionService.permissionDel(getPageData());
	}
	
	@RequestMapping("/permissionEditicon")
	@ResponseBody
	public String editicon() throws Exception{
		return permissionService.editicon(getPageData());
	}
	
	@RequestMapping("/permissionAdd")
	public String toAdd(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		String menuId = Tools.getStringValue(pd.get("PARENT_ID"));
		Map<String, Object> result = permissionService.findOneService(menuId);
		map.put("parentName", result);
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("PARENT_ID", menuId);
		map.put("url", Tools.getStringValue(pd.get("url")));
		pd.put("TABLE_NAME", "JT_MENU");
		pd.put("TABLE_COLUMN", "MENU_TYPE");
		map.put("menutypeList", dictionariesService.listDicService(pd));
		return "/system/permission/permission_edit";
	}
	
	@RequestMapping("/permissionEdit")
	public String toEdit(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		String menuId = Tools.getStringValue(pd.get("TREE_ID"));
		map.put("TREE_ID", menuId);
		map.put("PARENT_ID", Tools.getStringValue(pd.get("PARENT_ID")));
		Map<String, Object> result = permissionService.findOneService(menuId);
		map.put("result", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		
		pd.put("TABLE_NAME", "JT_MENU");
		pd.put("TABLE_COLUMN", "MENU_TYPE");
		map.put("menutypeList", dictionariesService.listDicService(pd));
		return "/system/permission/permission_edit";
	}
	
}
