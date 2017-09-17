package com.sdd.service.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.sdd.dao.DaoSupport;
import com.sdd.model.PageData;
import com.sdd.model.system.User;
import com.sdd.util.Constants;
import com.sdd.util.MD5;
import com.sdd.util.RightsHelper;
import com.sdd.util.Tools;

@Service
public class UserService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	public User findByName(String name) throws Exception {
		User user = (User) dao.findForObject("com.sdd.mapper.UserMapper.findByName", name);
		return user;
	}
	
	public List<Map<String, Object>> listService(int pageNum, int pageSize, PageData pd) throws Exception{
		PageHelper.startPage(pageNum, pageSize);
		Object object = dao.findForList("com.sdd.mapper.UserMapper.list", pd);
		return Tools.objToList(object);
	}
	
	@Value("${default.user.password}")
	private String password;
	
	public String saveService(PageData pd) throws Exception {
		//密码
		pd.put("PASSWORD", MD5.md5(password));
		dao.save("com.sdd.mapper.UserMapper.save", pd);
		return Constants.SUCCESS;
	}
	
	public int updateRightsService(PageData pd) throws Exception {
		String ids = Tools.getStringValue(pd.get("ids"));
		BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray(ids));//用菜单ID做权处理
		pd.put("RIGHTS", rights.toString());
		return (int) dao.update("com.sdd.mapper.UserMapper.updateRights", pd);
	}
	
	public int updateRoleRightsService(PageData pd) throws Exception {
		String ids = Tools.getStringValue(pd.get("ids"));
		BigInteger rights =new BigInteger("0");
		if(!"".equals(ids)){
			rights = RightsHelper.sumRights(Tools.str2StrArray(ids));
		}
		pd.put("ROLE_RIGHTS", rights.toString());
		return (int) dao.update("com.sdd.mapper.UserMapper.updateRoleRights", pd);
	}
	
	public Map<String, Object> editService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.UserMapper.listById", pd);
		return Tools.objToMap(object);
	}
	
	public Map<String, Object> loginUser(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.UserMapper.loginUser", pd);
		return Tools.objToMap(object);
	}
	
	public String updateService(PageData pd) throws Exception {
		dao.update("com.sdd.mapper.UserMapper.update", pd);
		return Constants.SUCCESS;
	}
	
	public String updateUserInfo(PageData pd) throws Exception {
		dao.update("com.sdd.mapper.UserMapper.updateUserInfo", pd);
		return Constants.SUCCESS;
	}
	
	public String passwdUpdate(PageData pd) throws Exception {
		String userId = Tools.getStringValue(pd.get("USER_ID"));
		if("".equals(userId)){
			String hisPasswd = Tools.getStringValue(pd.get("HIS_PASSWD"));
			Subject subject = SecurityUtils.getSubject();
			User user = (User) subject.getPrincipal();
			if(user.getPassword().equals(MD5.md5(hisPasswd))){
				pd.put("USER_ID", user.getUserId());
				String passwd = MD5.md5(Tools.getStringValue(pd.get("PASSWD")));
				pd.put("USER_PASSWD", passwd);
				dao.update("com.sdd.mapper.UserMapper.updatePasswd", pd);
				return Constants.SUCCESS;
			}else{
				return "原始密码不正确";
			}
		}else{
			String passwd = MD5.md5(Tools.getStringValue(pd.get("PASSWD")));
			pd.put("USER_PASSWD", passwd);
			dao.update("com.sdd.mapper.UserMapper.updatePasswd", pd);
			return Constants.SUCCESS;
		}
	}
	
	public int delService(PageData pd) throws Exception {
		return (int) dao.delete("com.sdd.mapper.UserMapper.delete", pd);
	}
	
	public List<Map<String, Object>> userRoleService(PageData pd) throws Exception{
		Object object = dao.findForObject("com.sdd.mapper.UserMapper.listById", pd);
		
		Map<String, Object> user = Tools.objToMap(object);
		String roleIdRights = Tools.getStringValue(user.get("ROLE_RIGHTS"));
		
		Object objList = dao.findForList("com.sdd.mapper.RoleMapper.listAll", pd);
		List<Map<String, Object>> list = Tools.objToList(objList);
		for(Map<String, Object> m : list){
			String roleId = Tools.getStringValue(m.get("ROLE_ID"));
			boolean b = RightsHelper.testRights(roleIdRights, roleId);
			m.put("select", b);
		}
		return list;
	}
	
	public List<Map<String, Object>> permissionService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.UserMapper.listById", pd);
		Map<String, Object> user = Tools.objToMap(object);
		String userRights = Tools.getStringValue(user.get("RIGHTS"));
		String roleRights = Tools.getStringValue(user.get("ROLE_RIGHTS"));
		userRights = "".equals(userRights) ? "0" : userRights;
		roleRights = "".equals(roleRights) ? "0" : roleRights;
		BigInteger rights = new BigInteger(userRights).or(new BigInteger(roleRights));
		
		return readMenu(getTreeList(rights.toString()), rights.toString());
	}
	
	private List<Map<String, Object>> readMenu(List<Map<String, Object>> menuList,String roleRights){
		for(int i=0;i<menuList.size();i++){
			int menuId = Tools.getIntValue(menuList.get(i), "MENU_ID");
			menuList.get(i).put("hasMenu",RightsHelper.testRights(roleRights, menuId));
			List<Map<String, Object>> sub = Tools.objToList(menuList.get(i).get("subMenu"));
			if(sub == null){
				sub = Collections.emptyList();
			}
			readMenu(sub, roleRights);
		}
		return menuList;
	}
	
	private List<Map<String, Object>> getTreeList(String roleRights) throws Exception {
		// 目标树
		List<Map<String, Object>> treeList = new ArrayList<Map<String, Object>>();
		// 所有的顶级节点
		List<Map<String, Object>> rootList = Tools.objToList(dao.findForList("com.sdd.mapper.PermissionMapper.getFaPermission", ""));
		// 所有的非顶级节点
		List<Map<String, Object>> childList = Tools.objToList(dao.findForList("com.sdd.mapper.PermissionMapper.getPermission", ""));
		Subject subject = SecurityUtils.getSubject();
		// 遍历顶级节点
		for (Map<String, Object> root : rootList) {
			Integer menuId =  MapUtils.getInteger(root, "MENU_ID");
			String menuUrl = Tools.getStringValue(root.get("MENU_URL"));
			if(subject.isPermitted(menuId.toString()+menuUrl)){
				// 构造子结点
				buildPermissionTree(subject, roleRights, root, childList);
				// 把根节点放到集合中
				treeList.add(root);
			}
		}
		return treeList;
	}

	private void buildPermissionTree(Subject subject, String roleRights, Map<String, Object> root, List<Map<String, Object>> childList) {
		Integer menuId =  MapUtils.getInteger(root, "MENU_ID");
		String menuUrl = Tools.getStringValue(root.get("MENU_URL"));
		if(subject.isPermitted(menuId.toString()+menuUrl)){
			root.put("target", "treeFrame");
			for (Map<String, Object> child : childList) {
				Integer childMenuId = MapUtils.getInteger(child, "MENU_ID");
				String childMenuUrl = Tools.getStringValue(child.get("MENU_URL"));
				//从非顶级节点中，为当前节点寻找子结点
				boolean equals = MapUtils.getInteger(child, "PARENT_ID").equals(menuId);
				if (equals && subject.isPermitted(childMenuId.toString()+childMenuUrl)) {
					List<Map<String, Object>> list = Tools.objToList(root
							.get("subMenu"));
					if (list == null) {
						list = new ArrayList<Map<String, Object>>();
						root.put("subMenu", list);
					}
					child.put("target", "treeFrame");
					list.add(child);
					//递归，为当前节点构造子结点
					buildPermissionTree(subject, roleRights, child, childList);
				}
			}
		}
	}
}
