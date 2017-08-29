package com.zyx.web.${model};

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageInfo;
import com.zyx.model.Page;
import com.zyx.model.PageData;
import com.zyx.service.${model}.${class}Service;
import com.zyx.util.Tools;
import com.zyx.web.WebController;
<#if hasSelect == 'true'>
import com.zyx.service.system.DictionariesService;
</#if>
/** 
 * 说明：
 * 创建人：kbky
 * 创建时间：
 */
@Controller("${model}${class}Controller")
@RequestMapping(value="/${model}")
public class ${class}Controller extends WebController {
	
	@Autowired
	private ${class}Service ${model}${class}Service;
	<#if hasSelect == 'true'>
	@Autowired
	private DictionariesService dictionariesService;
	</#if>
	
	/*
	 * 查询
	 */
	@RequestMapping("/${class?lower_case}List")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = ${model}${class}Service.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("${class?lower_case}List", list);
		map.put("pd", pd);
		return "/${model}/${class?lower_case}/${class?lower_case}_list";
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("/${class?lower_case}Save")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception{
		return ${model}${class}Service.saveService(getPageData());
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("/${class?lower_case}Update")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception{
		return ${model}${class}Service.updateService(getPageData());
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("/${class?lower_case}Del")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception{
		return ${model}${class}Service.delService(getPageData());
	}
	
	/*
	 * 跳转到新增页面
	 */
	@RequestMapping("/${class?lower_case}Add")
	public String add(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		<#list columnMap?keys as key>  
		<#if columnMap[key][1] == 'true'>
		pd.put("TABLE_NAME", "${tableName}");
		pd.put("TABLE_COLUMN", "${key}");
		map.put("${key?lower_case?replace("_","")}List", dictionariesService.listDicService(pd));
		</#if>
		</#list>
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/${model}/${class?lower_case}/${class?lower_case}_edit";
	}
	
	/*
	 * 跳转到修改页面
	 */
	@RequestMapping("/${class?lower_case}Edit")
	public String edit(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		Map<String, Object> result = ${model}${class}Service.editService(pd);
		map.put("${class?lower_case}", result);
		<#list columnMap?keys as key>  
		<#if columnMap[key][1] == 'true'>
		pd.put("TABLE_NAME", "${tableName}");
		pd.put("TABLE_COLUMN", "${key}");
		map.put("${key?lower_case?replace("_","")}List", dictionariesService.listDicService(pd));
		</#if>
		</#list>
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/${model}/${class?lower_case}/${class?lower_case}_edit";
	}
	
	/*
	 * 跳转到详情页面
	 */
	@RequestMapping("/${class?lower_case}Detail")
	public String detail(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		Map<String, Object> result = ${model}${class}Service.editService(pd);
		map.put("${class?lower_case}", result);
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/${model}/${class?lower_case}/${class?lower_case}_detail";
	}
}
