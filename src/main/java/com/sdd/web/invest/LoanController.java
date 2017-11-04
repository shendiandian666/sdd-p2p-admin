package com.sdd.web.invest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.github.pagehelper.PageInfo;
import com.sdd.model.Page;
import com.sdd.model.PageData;
import com.sdd.service.invest.LoanService;
import com.sdd.service.invest.PlatformService;
import com.sdd.service.system.DictionariesService;
import com.sdd.util.Tools;
import com.sdd.web.WebController;

/**
 * 说明： 创建人：kbky 创建时间：
 */
@Controller("investLoanController")
@RequestMapping(value = "/invest")
public class LoanController extends WebController {

	@Autowired
	private LoanService investLoanService;

	@Autowired
	private DictionariesService dictionariesService;
	
	/*
	 * 查询
	 */
	@RequestMapping("/loanList")
	public String list(Map<String, Object> map) throws Exception {
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = investLoanService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("loanList", list);
		map.put("pd", pd);
		return "/invest/loan/loan_list";
	}

	@Value("${p2p.domain}")
	private String domain;
	/*
	 * 保存
	 */
	@RequestMapping("/loanSave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		String img = pd.getString("platform_img");
		pd.put("platform_img", domain + img);
		return investLoanService.saveService(pd);
	}

	/*
	 * 修改
	 */
	@RequestMapping("/loanUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		String img = pd.getString("platform_img");
		if(!img.contains("http")){
			pd.put("platform_img", domain + img);
		}
		return investLoanService.updateService(pd);
	}

	/*
	 * 删除
	 */
	@RequestMapping("/loanDel")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception {
		return investLoanService.delService(getPageData());
	}

	/*
	 * 跳转到新增页面
	 */
	@RequestMapping("/loanAdd")
	public String add(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		
		pd.put("TABLE_NAME", "p2p_platform");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/loan/loan_edit";
	}

	/*
	 * 跳转到修改页面
	 */
	@RequestMapping("/loanEdit")
	public String edit(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		
		pd.put("TABLE_NAME", "p2p_platform");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		Map<String, Object> result = investLoanService.editService(pd);
		map.put("loan", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/loan/loan_edit";
	}

	/*
	 * 跳转到详情页面
	 */
	@RequestMapping("/loanDetail")
	public String detail(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		Map<String, Object> result = investLoanService.editService(pd);
		map.put("loan", result);
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/loan/loan_detail";
	}

}
