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
import com.sdd.service.invest.PlatformService;
import com.sdd.service.system.DictionariesService;
import com.sdd.util.Tools;
import com.sdd.web.WebController;

/**
 * 说明： 创建人：kbky 创建时间：
 */
@Controller("investPlatformController")
@RequestMapping(value = "/invest")
public class PlatformController extends WebController {

	@Autowired
	private PlatformService investPlatformService;

	@Autowired
	private DictionariesService dictionariesService;
	
	/*
	 * 查询
	 */
	@RequestMapping("/platformList")
	public String list(Map<String, Object> map) throws Exception {
		PageData pd = this.getPageData();
		int pageNum = Page.getPageNum(pd);
		int pageSize = Page.getPageSize(pd);
		List<Map<String, Object>> list = investPlatformService.listService(pageNum, pageSize, pd);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(list);
		map.put("page", pageInfo);
		map.put("platformList", list);
		map.put("pd", pd);
		return "/invest/platform/platform_list";
	}

	@Value("${p2p.domain}")
	private String domain;
	/*
	 * 保存
	 */
	@RequestMapping("/platformSave")
	@ResponseBody
	public String save(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		String img = pd.getString("platform_img");
		pd.put("platform_img", domain + img);
		return investPlatformService.saveService(pd);
	}

	/*
	 * 修改
	 */
	@RequestMapping("/platformUpdate")
	@ResponseBody
	public String update(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		String img = pd.getString("platform_img");
		pd.put("platform_img", domain + img);
		return investPlatformService.updateService(pd);
	}

	/*
	 * 删除
	 */
	@RequestMapping("/platformDel")
	@ResponseBody
	public String del(Map<String, Object> map) throws Exception {
		return investPlatformService.delService(getPageData());
	}

	/*
	 * 跳转到新增页面
	 */
	@RequestMapping("/platformAdd")
	public String add(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		
		pd.put("TABLE_NAME", "p2p_platform");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		map.put("action", "Save");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/platform/platform_edit";
	}

	/*
	 * 跳转到修改页面
	 */
	@RequestMapping("/platformEdit")
	public String edit(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		
		pd.put("TABLE_NAME", "p2p_platform");
		pd.put("TABLE_COLUMN", "status");
		map.put("statusList", dictionariesService.listDicService(pd));
		
		Map<String, Object> result = investPlatformService.editService(pd);
		map.put("platform", result);
		map.put("action", "Update");
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/platform/platform_edit";
	}

	/*
	 * 跳转到详情页面
	 */
	@RequestMapping("/platformDetail")
	public String detail(Map<String, Object> map) throws Exception {
		PageData pd = getPageData();
		Map<String, Object> result = investPlatformService.editService(pd);
		map.put("platform", result);
		map.put("pd", pd);
		map.put("url", Tools.getStringValue(pd.get("url")));
		return "/invest/platform/platform_detail";
	}

	String uploadPath;

	@Value("${p2p.localLocation}")
	private String localLocation;
	@Value("${p2p.remoteLocation}")
	private String remoteLocation;
	
	@RequestMapping("/upload")
	@ResponseBody
	public String plupload(@RequestParam MultipartFile file, HttpServletRequest request, HttpSession session) {
		String name = request.getParameter("name");
		try {
			Integer chunk = 0, chunks = 0;
			if(null != request.getParameter("chunk") && !request.getParameter("chunk").equals("")){
				chunk = Integer.valueOf(request.getParameter("chunk"));
			}
			if(null != request.getParameter("chunks") && !request.getParameter("chunks").equals("")){
				chunks = Integer.valueOf(request.getParameter("chunks"));
			}
			System.out.println("chunk:[" + chunk + "] chunks:[" + chunks + "]");
			//检查文件目录，不存在则创建
			String relativePath = localLocation + "platform/logo";//"/Users/Neo/work";
			//String realPath = session.getServletContext().getRealPath("");
			File folder = new File(relativePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			//目标文件 
			File destFile = new File(folder, name);
			//文件已存在删除旧文件（上传了同名的文件） 
	        if (chunk == 0 && destFile.exists()) {  
	        	destFile.delete();  
	        	destFile = new File(folder, name);
	        }
	        //合成文件
	        appendFile(file.getInputStream(), destFile);  
	        if (chunk == chunks - 1) {  
	            System.out.println("上传完成");
	        }else {
	        	System.out.println("还剩["+(chunks-1-chunk)+"]个块文件");
	        }
			
		} catch (IOException e) {
			e.getMessage();
		}
		
		return remoteLocation + "platform/logo/" + name;
	}
	
	private static final int BUFFER_SIZE = 100 * 1024;
	
	private void appendFile(InputStream in, File destFile) {
		OutputStream out = null;
		try {
			// plupload 配置了chunk的时候新上传的文件append到文件末尾
			if (destFile.exists()) {
				out = new BufferedOutputStream(new FileOutputStream(destFile, true), BUFFER_SIZE); 
			} else {
				out = new BufferedOutputStream(new FileOutputStream(destFile),BUFFER_SIZE);
			}
			in = new BufferedInputStream(in, BUFFER_SIZE);
			
			int len = 0;
			byte[] buffer = new byte[BUFFER_SIZE];			
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {		
			try {
				if (null != in) {
					in.close();
				}
				if(null != out){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
