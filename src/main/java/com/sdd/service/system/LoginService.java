package com.sdd.service.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdd.util.Tools;

@Service
public class LoginService {

	@Autowired
	private MenuService menuService;

	/**
	 * 通过递归拼接用户拥有的权限菜单
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public StringBuilder mainService(HttpServletRequest request) throws Exception {
		StringBuilder treeSb = new StringBuilder();
		// 所有的顶级节点
		List<Map<String, Object>> rootList = menuService.getFaMenus();
		// 所有的非顶级节点
		List<Map<String, Object>> childList = menuService.getMenus();
		treeSb.append("<li class=''>");
		treeSb.append("<a href='" + request.getContextPath() + "/system/main'>");
		treeSb.append("<i class='menu-icon fa fa-tachometer'></i>");
		treeSb.append("<span class='menu-text'>后台首页</span>");
		treeSb.append("</a>");
		treeSb.append("<b class='arrow'></b>");
		treeSb.append("</li>");
		// 遍历顶级节点
		for (Map<String, Object> root : rootList) {
			StringBuilder childSb = new StringBuilder();
			// 构造子结点
			buildMenuHtml(request, childSb, root, childList);
			childSb.append("");
			// 把根节点放到集合中
			treeSb.append(childSb);
		}
		return treeSb;
	}

	private void buildMenuHtml(HttpServletRequest request, StringBuilder childSb, 
			Map<String, Object> rootNode, List<Map<String, Object>> childList) {
		Subject currentUser = SecurityUtils.getSubject();
		Integer menuId = MapUtils.getInteger(rootNode, "MENU_ID");
		String menuIcon = MapUtils.getString(rootNode, "MENU_ICON");
		String menuUrl = MapUtils.getString(rootNode, "MENU_URL");
		menuIcon = menuIcon == null ? "menu-icon fa fa-leaf black" : menuIcon;
		String menuName = MapUtils.getString(rootNode, "MENU_NAME");
		if (currentUser.isPermitted(menuId + menuUrl)) {
			boolean hasChild = false;
			for (Map<String, Object> child : childList) {
				boolean equals = MapUtils.getInteger(child, "PARENT_ID").equals(menuId);
				String childMenuId = MapUtils.getString(child, "MENU_ID");
				String childMenuUrl = MapUtils.getString(child, "MENU_URL");
				if (equals && currentUser.isPermitted(childMenuId + childMenuUrl)) {
					hasChild = true;
					break;
				}
			}
			childSb.append("<li class=''  id='" + menuId + "'>");
			childSb.append("<a style='cursor:pointer;' ");
			if (!"#".equals(menuUrl) && !"".equals(menuUrl)) {
				childSb.append(" target='mainFrame' ");
				String project = request.getContextPath();
				childSb.append(" onclick=\"siMenu(" + "'z" + menuId + "','lm" + menuId + 
						"','" + menuName + "','" + project + menuUrl + "')\"");
			}
			if (hasChild) {
				childSb.append(" class='dropdown-toggle'");
			}
			childSb.append(">");
			childSb.append("<i class='" + menuIcon + "'></i>");
			childSb.append("<span class='menu-text'>" + menuName + "</span>");
			if (hasChild) {
				childSb.append("<b class='arrow fa fa-angle-down'></b></a>");
				childSb.append("<b class='arrow'></b>");
				childSb.append("<ul class='submenu'>");
				for (Map<String, Object> child : childList) {
					// 从非顶级节点中，为当前节点寻找子结点
					boolean equals = MapUtils.getInteger(child, "PARENT_ID").equals(menuId);
					String childMenuId = MapUtils.getString(child, "MENU_ID");
					String childMenuUrl = MapUtils.getString(child, "MENU_URL");
					if (equals && currentUser.isPermitted(childMenuId + childMenuUrl)) {
						List<Map<String, Object>> list = Tools.objToList(rootNode.get("children"));
						if (list == null) {
							list = new ArrayList<Map<String, Object>>();
							rootNode.put("children", list);
						}
						list.add(child);
						buildMenuHtml(request, childSb, child, childList);
					}
				}
				childSb.append("</ul>");
			} else {
				childSb.append("</a><b class='arrow'></b>");
			}
			childSb.append("</li>");
		}

	}
}
