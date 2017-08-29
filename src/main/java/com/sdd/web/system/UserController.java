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
import com.sdd.service.system.DictionariesService;
import com.sdd.service.system.OrganService;
import com.sdd.service.system.UserService;
import com.sdd.util.Constants;
import com.sdd.util.JsonTool;
import com.sdd.util.Tools;
import com.sdd.web.WebController;

/**
 * 用户管理
 * @author Administrator
 *
 */
@Controller
public class UserController extends WebController {

	@Autowired
	private UserService userService;
	@Autowired
	private OrganService organService;
	@Autowired
	private DictionariesService dictionariesService;
	
	/*
	 * 查询用户列表
	 */
	@RequestMapping("/userList")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = userService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("userList", list);
		map.put("pd", pd);
		return "/system/user/user_list";
	}
	
	/*
	 * 用户权限
	 */
	@RequestMapping("/userRights")
	public String permission(Map<String, Object> map)throws Exception{
		PageData pd = getPageData();
		map.put("USER_ID", Tools.getStringValue(pd.get("USER_ID")));
		List<Map<String, Object>> list = userService.permissionService(pd);
		String json = JsonTool.beanToJson(list);
		json = json.replaceAll("MENU_ID", "id").replaceAll("PARENT_ID", "pId")
				.replaceAll("MENU_NAME", "name").replaceAll("subMenu", "nodes")
				.replaceAll("hasMenu", "checked");
		map.put("zTreeNodes", json);
		return "/system/user/user_tree";
	}
	
	/*
	 * 用户角色
	 */
	@RequestMapping("/userRole")
	public String userRole(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		map.put("USER_ID", Tools.getStringValue(pd.get("USER_ID")));
		List<Map<String, Object>> list = userService.userRoleService(pd);
		map.put("userRoleList", list);
		return "/system/user/role_tree";
	}
	
	/*
	 * 添加用户
	 */
	@RequestMapping("/userSave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception{
		return userService.saveService(getPageData());
	}
	
	/*
	 * 修改用户
	 */
	@RequestMapping("/userUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception{
		return userService.updateService(getPageData());
	}
	
	@RequestMapping("/userInfoUpdate")
	@ResponseBody
	public String updateUserInfo(Map<String, Object> map) throws Exception{
		return userService.updateUserInfo(getPageData());
	}
	
	/*
	 * 修改密码
	 */
	@RequestMapping("/userPasswdUpdate")
	@ResponseBody
	public String passwdUpdate(Map<String, Object> map) throws Exception{
		return userService.passwdUpdate(getPageData());
	}
	
	@RequestMapping("/userRightsUpdate")
	@ResponseBody
	public boolean rightsUpdate(Map<String, Object> map) throws Exception {
		int count = userService.updateRightsService(getPageData());
		if(count == 1){
			return true;
		}else{
			return false;
		}
	}
	
	@RequestMapping("/userRoleRightsUpdate")
	@ResponseBody
	public boolean roleRightsUpdate(Map<String, Object> map) throws Exception {
		int count = userService.updateRoleRightsService(getPageData());
		if(count == 1){
			return true;
		}else{
			return false;
		}
	}
	
	@RequestMapping("/userDel")
	public String del(Map<String, Object> map) throws Exception{
		userService.delService(getPageData());
		map.put("msg", Constants.SUCCESS);
		return "/system/save_result";
	}
	
	@RequestMapping("/userAdd")
	public String add(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		map.put("organDicList", organService.organDic(pd));
		
		pd.put("TABLE_NAME", "JT_USER");
		pd.put("TABLE_COLUMN", "STATUS");
		map.put("statusList", dictionariesService.listDicService(pd));
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/system/user/user_edit";
	}
	
	@RequestMapping("/userEdit")
	public String edit(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		map.put("organDicList", organService.organDic(pd));
		Map<String, Object> result = userService.editService(pd);
		pd.put("TABLE_NAME", "JT_USER");
		pd.put("TABLE_COLUMN", "STATUS");
		map.put("statusList", dictionariesService.listDicService(pd));
		map.put("user", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/system/user/user_edit";
	}
	
	@RequestMapping("/userInfo")
	public String info(Map<String, Object> map) throws Exception{
		Map<String, Object> result = userService.loginUser(getPageData());
		map.put("user", result);
		return "/system/user/user_info";
	}
	
	@RequestMapping("/userPasswd")
	public String userPasswd(Map<String, Object> map) throws Exception {
		return "/system/user/user_passwd";
	}
	
	@RequestMapping("/userReset")
	public String resetPwd(Map<String, Object> map) throws Exception{
		map.put("USER_ID", Tools.getStringValue(getPageData().get("USER_ID")));
		return "/system/user/user_reset";
	}
}
