package com.sdd.service.invest;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.sdd.dao.DaoSupport;
import com.sdd.model.PageData;
import com.sdd.util.Constants;
import com.sdd.util.Tools;

/** 
 * 说明： 
 * 创建人：kbky
 * 创建时间：
 * @version
 */
@Service("investActivityService")
public class ActivityService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	public List<Map<String, Object>> listDicService() throws Exception{
		Object object = dao.findForList("com.sdd.mapper.invest.ActivityMapper.listDic", "");
		return Tools.objToList(object);
	}
	
	/*
	 * 查询
	 */
	public List<Map<String, Object>> listService(int pageNum, int pageSize, PageData pd) throws Exception{
		PageHelper.startPage(pageNum, pageSize);
		Object object = dao.findForList("com.sdd.mapper.invest.ActivityMapper.list", pd);
		return Tools.objToList(object);
	}
	
	/*
	 * 保存
	 */
	public String saveService(PageData pd) throws Exception {
		int count = (int) dao.findForObject("com.sdd.mapper.invest.ActivityMapper.isExists", pd);
		if(count > 0){
			return "主键ID已存在!";
		}else{
			dao.save("com.sdd.mapper.invest.ActivityMapper.save", pd);
			return Constants.SUCCESS;
		}
	}
	
	/*
	 * 修改页面数据查询
	 */
	public Map<String, Object> editService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.invest.ActivityMapper.listById", pd);
		return Tools.objToMap(object);
	}
	
	/*
	 * 修改
	 */
	public String updateService(PageData pd) throws Exception {
		int count = (int) dao.findForObject("com.sdd.mapper.invest.ActivityMapper.isExists", pd);
		if(count > 0){
			dao.update("com.sdd.mapper.invest.ActivityMapper.update", pd);
			return Constants.SUCCESS;
		}else{
			return "记录已被其他用户删除!";		
		}
	}
	
	/**
	 * 重置最佳方案
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public String bestService(PageData pd) throws Exception {
		//查询最佳方案
		Map<String, Object> best = (Map<String, Object>) dao.findForObject("com.sdd.mapper.invest.ActivityMapper.bestPlan", pd);
		if(best != null){
			pd.put("bestName", best.get("name"));
			pd.put("bestDeposit", best.get("depositDisplay_s"));
			pd.put("bestMoney", best.get("totalIncome"));
			pd.put("bestInterest", best.get("interest"));
			dao.update("com.sdd.mapper.invest.ActivityMapper.update", pd);
		}
		return Constants.SUCCESS;
	}
	
	/*
	 * 删除
	 */
	public String delService(PageData pd) throws Exception {
		dao.delete("com.sdd.mapper.invest.ActivityMapper.delete", pd);
		return Constants.SUCCESS;
	}
	
}

