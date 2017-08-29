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
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.sdd.dao.DaoSupport;
import com.sdd.model.PageData;
import com.sdd.util.RightsHelper;
import com.sdd.util.Tools;

@Service
public class RoleService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	public List<Map<String, Object>> listAllService(PageData pd) throws Exception {
		Object object = dao.findForList("com.sdd.mapper.RoleMapper.listAll", pd);
		return Tools.objToList(object);
	}
	
	public List<Map<String, Object>> listService(int pageNum, int pageSize, PageData pd) throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		Object object = dao.findForList("com.sdd.mapper.RoleMapper.list", pd);
		return Tools.objToList(object);
	}
	
	public int saveService(PageData pd) throws Exception {
		return dao.save("com.sdd.mapper.RoleMapper.save", pd);
	}
	
	public Map<String, Object> selectOneService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.RoleMapper.listById", pd);
		return Tools.objToMap(object);
	}
	
	public int updateService(PageData pd) throws Exception {
		return (int) dao.update("com.sdd.mapper.RoleMapper.update", pd);
	}
	
	public int delService(PageData pd) throws Exception {
		return (int) dao.delete("com.sdd.mapper.RoleMapper.delete", pd);
	}
	
	public List<Map<String, Object>> permissionService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.RoleMapper.listById", pd);
		Map<String, Object> role = Tools.objToMap(object);
		String roleRights = Tools.getStringValue(role.get("ROLE_RIGHTS"));
		
		//return getTreeList(roleRights);
		return readMenu(getTreeList(roleRights), roleRights);
	}
	
	public List<Map<String, Object>> readMenu(List<Map<String, Object>> menuList,String roleRights){
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
	
	public int permissionSaveService(PageData pd) throws Exception {
		String menuIds = Tools.getStringValue(pd.get("menuIds"));
		BigInteger rights = RightsHelper.sumRights(Tools.str2StrArray(menuIds));//用菜单ID做权处理
		pd.put("ROLE_RIGHTS", rights.toString());
		return (int) dao.update("com.sdd.mapper.RoleMapper.updateRights", pd);
	}
	
	public List<Map<String, Object>> getTreeList(String roleRights) throws Exception {
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
