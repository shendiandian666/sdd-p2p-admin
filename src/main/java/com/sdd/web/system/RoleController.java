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
import com.sdd.service.system.RoleService;
import com.sdd.util.Constants;
import com.sdd.util.JsonTool;
import com.sdd.util.Tools;
import com.sdd.web.WebController;

@Controller
public class RoleController extends WebController {

	@Autowired
	private RoleService roleService;
	
	@RequestMapping("/roleList")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = roleService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("roleList", list);
		map.put("pd", pd);
		return "/system/role/role_list";
	}
	
	@RequestMapping("/roleSave")
	public String save(Map<String, Object> map) throws Exception{
		roleService.saveService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	@RequestMapping("/roleUpdate")
	public String update(Map<String, Object> map) throws Exception{
		roleService.updateService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	@RequestMapping("/roleDel")
	public String del(Map<String, Object> map) throws Exception{
		roleService.delService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	@RequestMapping("/roleAdd")
	public String add(Map<String, Object> map) throws Exception{
		map.put("action", "Save");
		return "/system/role/role_edit";
	}
	
	@RequestMapping("/roleEdit")
	public String edit(Map<String, Object> map) throws Exception{
		Map<String, Object> result = roleService.selectOneService(getPageData());
		map.put("role", result);
		map.put("action", "Update");
		return "/system/role/role_edit";
	}
	
	@RequestMapping("/rolePermission")
	public String permission(Map<String, Object> map)throws Exception{
		PageData pd = getPageData();
		map.put("ROLE_ID", Tools.getStringValue(pd.get("ROLE_ID")));
		List<Map<String, Object>> list = roleService.permissionService(pd);
		String json = JsonTool.beanToJson(list);
		json = json.replaceAll("MENU_ID", "id").replaceAll("PARENT_ID", "pId")
				.replaceAll("MENU_NAME", "name").replaceAll("subMenu", "nodes")
				.replaceAll("hasMenu", "checked");
		map.put("zTreeNodes", json);
		return "/system/role/tree";
	}
	@RequestMapping("/rolePermissionSave")
	@ResponseBody
	public boolean permissionSave(Map<String, Object> map) throws Exception {
		int count = roleService.permissionSaveService(getPageData());
		if(count == 1){
			return true;
		}else{
			return false;
		}
	}
	
}
