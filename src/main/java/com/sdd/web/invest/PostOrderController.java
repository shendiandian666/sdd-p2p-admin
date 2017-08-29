package com.sdd.web.invest;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.pagehelper.PageInfo;
import com.sdd.model.Page;
import com.sdd.model.PageData;
import com.sdd.service.invest.PlatformService;
import com.sdd.service.invest.PostOrderService;
import com.sdd.service.system.DictionariesService;
import com.sdd.util.Tools;
import com.sdd.web.WebController;
/** 
 * 说明：
 * 创建人：kbky
 * 创建时间：
 */
@Controller("investPostOrderController")
@RequestMapping(value="/invest")
public class PostOrderController extends WebController {
	
	@Autowired
	private PlatformService platformService;
	
	@Autowired
	private PostOrderService investPostOrderService;
	
	@Autowired
	private DictionariesService dictionariesService;
	
	/*
	 * 查询
	 */
	@RequestMapping("/postorderList")
	public String list(Map<String, Object> map) throws Exception{
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = investPostOrderService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("postorderList", list);
		map.put("pd", pd);
		map.put("platform_id", pd.get("platform_id"));
		map.put("platformList", platformService.listDicService());
		
		return "/invest/postorder/postorder_list";
	}
	
	/*
	 * 保存
	 */
	@RequestMapping("/postorderSave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception{
		return investPostOrderService.saveService(getPageData());
	}
	
	/*
	 * 审核通过
	 */
	@RequestMapping("/postorderPass")
	@ResponseBody
	public String pass(Map<String, Object> map) throws Exception{
		return investPostOrderService.passService(getPageData());
	}
	
	/*
	 * 审核不通过
	 */
	@RequestMapping("/postorderNoPass")
	@ResponseBody
	public String noPass(Map<String, Object> map) throws Exception{
		return investPostOrderService.passErrorService(getPageData());
	}
	
	/*
	 * 修改
	 */
	@RequestMapping("/postorderUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception{
		return investPostOrderService.updateService(getPageData());
	}
	
	/*
	 * 删除
	 */
	@RequestMapping("/postorderDel")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception{
		return investPostOrderService.delService(getPageData());
	}
	
	/*
	 * 跳转到新增页面
	 */
	@RequestMapping("/postorderAdd")
	public String add(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		
		pd.put("TABLE_NAME", "p2p_post_order");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/postorder/postorder_edit";
	}
	
	/*
	 * 跳转到修改页面
	 */
	@RequestMapping("/postorderEdit")
	public String edit(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		
		pd.put("TABLE_NAME", "p2p_post_order");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		Map<String, Object> result = investPostOrderService.editService(pd);
		map.put("postorder", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/postorder/postorder_edit";
	}
	
	/*
	 * 跳转到详情页面
	 */
	@RequestMapping("/postorderDetail")
	public String detail(Map<String, Object> map) throws Exception{
		PageData pd = getPageData();
		Map<String, Object> result = investPostOrderService.editService(pd);
		map.put("postorder", result);
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/postorder/postorder_detail";
	}
}
