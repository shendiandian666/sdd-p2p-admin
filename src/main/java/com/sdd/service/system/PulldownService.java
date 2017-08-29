package com.sdd.service.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.sdd.dao.DaoSupport;
import com.sdd.model.PageData;
import com.sdd.util.Tools;

@Service
public class PulldownService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	 * 列表查询
	 */
	public List<Map<String, Object>> listService(int pageNum, int pageSize, PageData pd) throws Exception{
		PageHelper.startPage(pageNum, pageSize);
		Object object = dao.findForList("com.sdd.mapper.PulldownMapper.list", pd);
		return Tools.objToList(object);
	}
	
	/*
	 * 保存
	 */
	public int saveService(PageData pd) throws Exception {
		return dao.save("com.sdd.mapper.PulldownMapper.save", pd);
	}
	
	public Map<String, Object> editService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.PulldownMapper.listById", pd);
		return Tools.objToMap(object);
	}
	
	/*
	 * 修改
	 */
	public int updateService(PageData pd) throws Exception {
		return (int) dao.update("com.sdd.mapper.PulldownMapper.update", pd);
	}
	
	/*
	 * 删除
	 */
	public int delService(PageData pd) throws Exception {
		return (int) dao.delete("com.sdd.mapper.PulldownMapper.delete", pd);
	}
	
	/*
	 * 生成数据字典树形菜单
	 */
	public List<Map<String, Object>> getTreeList(HttpServletRequest request, PageData pd) throws Exception {
		String dicId = pd.getString("DIC_ID");
		String projectPath = request.getContextPath();
		// 目标树
		List<Map<String, Object>> treeList = new ArrayList<Map<String, Object>>();
		// 所有的顶级节点
		List<Map<String, Object>> rootList = 
				Tools.objToList(dao.findForList("com.sdd.mapper.DictionariesMapper.getFaDictionaries", ""));
		// 所有的非顶级节点
		List<Map<String, Object>> childList = 
				Tools.objToList(dao.findForList("com.sdd.mapper.DictionariesMapper.getDictionaries", ""));
		// 遍历顶级节点
		for (Map<String, Object> root : rootList) {
			// 构造子结点
			buildDictionariesTree(projectPath, root, childList, dicId);
			// 把根节点放到集合中
			treeList.add(root);
		}
		return treeList;
	}
	
	private void buildDictionariesTree(String projectPath, Map<String, Object> root, List<Map<String, Object>> childList, String dicId) {
		Integer menuId =  MapUtils.getInteger(root, "DICTIONARIES_ID");
		if(menuId.toString().equals(dicId)){
			root.put("checked", true);
		}
		for (Map<String, Object> child : childList) {
			Integer childMenuId =  MapUtils.getInteger(child, "DICTIONARIES_ID");
			if(childMenuId.toString().equals(dicId)){
				child.put("checked", true);
			}
			//从非顶级节点中，为当前节点寻找子结点
			boolean equals = MapUtils.getInteger(child, "PARENT_ID").equals(menuId);
			if (equals) {
				List<Map<String, Object>> list = Tools.objToList(root.get("children"));
				if (list == null) {
					list = new ArrayList<Map<String, Object>>();
					root.put("children", list);
				}
				list.add(child);
				//递归，为当前节点构造子结点
				buildDictionariesTree(projectPath, child, childList, dicId);
			}
		}
	}
	
	/*
	 * 修改数据字典
	 */
	public int updateDic(PageData pd) throws Exception {
		return (int) dao.update("com.sdd.mapper.PulldownMapper.updateDic", pd);
	}
	
}
