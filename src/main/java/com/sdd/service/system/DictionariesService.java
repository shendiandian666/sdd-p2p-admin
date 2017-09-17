package com.sdd.service.system;

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
public class DictionariesService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	 * 根据表名与字段名获取下拉列表的值
	 */
	public List<Map<String, Object>> listDicService(PageData pd) throws Exception {
		Object object = dao.findForList("com.sdd.mapper.DictionariesMapper.listDic", pd);
		return Tools.objToList(object);
	}
	
	public List<Map<String, Object>> listDicByParent(PageData pd) throws Exception {
		Object object = dao.findForList("com.sdd.mapper.DictionariesMapper.listDicById", pd);
		return Tools.objToList(object);
	}
	
	/*
	 * 获取根目录
	 */
	public List<Map<String, Object>> getRootList(HttpServletRequest request) throws Exception {
		String projectPath = request.getContextPath();
		List<Map<String, Object>> rootList = Tools
				.objToList(dao.findForList("com.sdd.mapper.DictionariesMapper.getRoot", ""));
		//查询是否有子节点
		List<Map<String, Object>> childList = Tools
				.objToList(dao.findForList("com.sdd.mapper.DictionariesMapper.getDictionaries", ""));
		for(Map<String, Object> root : rootList){
			Integer menuId =  MapUtils.getInteger(root, "B_ID");
			root.put("B_URL", projectPath + "/dictionariesRight?TREE_ID="+menuId);
			root.put("target", "treeFrame");
			for(Map<String, Object> child : childList){
				//从非顶级节点中，为当前节点寻找子结点
				boolean equals = MapUtils.getInteger(child, "PARENT_ID").equals(menuId);
				if(equals){
					root.put("isParent", true);
				}
			}
		}
		return rootList;
	}
	
	/*
	 * 获取子节点
	 */
	public List<Map<String, Object>> getNodes(HttpServletRequest request,PageData pd) throws Exception {
		String parentId = Tools.getStringValue(pd.get("parent_id"));
		if("".equals(parentId)){
			pd.put("parent_id", "0");
		}
		String projectPath = request.getContextPath();
		String parent_id = pd.getString("parent_id");
		List<Map<String, Object>> rootList = Tools
				.objToList(dao.findForList("com.sdd.mapper.DictionariesMapper.getNodes", parent_id));
		//查询是否有子节点
		List<Map<String, Object>> childList = Tools
				.objToList(dao.findForList("com.sdd.mapper.DictionariesMapper.getDictionaries", ""));
		for(Map<String, Object> root : rootList){
			Integer menuId =  MapUtils.getInteger(root, "B_ID");
			root.put("B_URL", projectPath + "/dictionariesRight?TREE_ID="+menuId);
			root.put("target", "treeFrame");
			for(Map<String, Object> child : childList){
				//从非顶级节点中，为当前节点寻找子结点
				boolean equals = MapUtils.getInteger(child, "PARENT_ID").equals(menuId);
				if(equals){
					root.put("isParent", true);
				}
			}
		}
		return rootList;
	}
	
	public Map<String, Object> findOneService(PageData pd) throws Exception{
		Object object = dao.findForObject("com.sdd.mapper.DictionariesMapper.findById", pd);
		return Tools.objToMap(object);
	}
	
	/*
	 * 获取查询列表
	 */
	public List<Map<String, Object>> dictionariesRight(int pageNum, int pageSize, PageData pd) throws Exception{
		String parentId = Tools.getStringValue(pd.get("TREE_ID"));
		if("".equals(parentId)){
			pd.put("TREE_ID", "0");
		}
		PageHelper.startPage(pageNum, pageSize);
		List<Map<String, Object>> list = 
				Tools.objToList(dao.findForList("com.sdd.mapper.DictionariesMapper.getDictionariesById", pd));
		return list;
	}
	
	/*
	 * 保存
	 */
	public int saveService(PageData pd) throws Exception {
		String parentId = Tools.getStringValue(pd.get("PARENT_ID"));
		if("".equals(parentId)){
			pd.put("PARENT_ID", "0");
		}
		return dao.save("com.sdd.mapper.DictionariesMapper.save", pd);
	}
	
	/*
	 * 修改
	 */
	public int updateService(PageData pd) throws Exception {
		return (int) dao.update("com.sdd.mapper.DictionariesMapper.update", pd);
	}
	
	/*
	 * 删除
	 */
	public int delService(PageData pd) throws Exception {
		Map<String, Object> map = (Map<String, Object>) dao.findForObject("com.sdd.mapper.DictionariesMapper.findById", pd);
		
		
		return (int) dao.delete("com.sdd.mapper.DictionariesMapper.delete", pd);
	}
	
}
