package com.sdd.service.invest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service("investPostOrderService")
public class PostOrderService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	 * 查询
	 */
	public List<Map<String, Object>> listService(int pageNum, int pageSize, PageData pd) throws Exception{
		PageHelper.startPage(pageNum, pageSize);
		Object object = dao.findForList("com.sdd.mapper.invest.PostOrderMapper.list", pd);
		return Tools.objToList(object);
	}
	
	/*
	 * 保存
	 */
	public String saveService(PageData pd) throws Exception {
		int count = (int) dao.findForObject("com.sdd.mapper.invest.PostOrderMapper.isExists", pd);
		if(count > 0){
			return "主键ID已存在!";
		}else{
			dao.save("com.sdd.mapper.invest.PostOrderMapper.save", pd);
			return Constants.SUCCESS;
		}
	}
	
	/*
	 * 审核通过
	 */
	@Transactional
	public String passService(PageData pd) throws Exception {
		//查询交单数据(判断状态)
		Object object = dao.findForObject("com.sdd.mapper.invest.PostOrderMapper.listById", pd);
		if(object == null){
			return "交单记录不存在!";
		}
		Map<String, Object> order = Tools.objToMap(object);
		String status = Tools.getStringValue(order.get("status"));
		if(!"2".equals(status)){
			return "只能通过<<审核中>>状态的数据!";
		}
		String account = Tools.getStringValue(order.get("account"));
		//cash_back
		Double cashBack = order.get("cash_back") == null ? 0 : Double.valueOf(order.get("cash_back").toString());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("cash_back", cashBack);
		//判断用户待提现余额是否足够,不足够需要插入预警表
		int count = (int) dao.findForObject("com.sdd.mapper.invest.AccountMapper.isPass", params);
		if(count != 1){
			Map<String, Object> warnMap = new HashMap<String, Object>();
			warnMap.put("account", account);
			warnMap.put("remark", "账号异常<待返现余额不足>,请查明原因!");
			dao.save("com.sdd.mapper.invest.PostOrderMapper.insertWarn", warnMap);
			return "账号异常<待返现余额不足>,请查明原因!";
		}
		//修改交单状态为(1已完成)
		Map<String, Object> orderParams = new HashMap<String, Object>();
		orderParams.put("ID", pd.getString("ID"));
		orderParams.put("status", "1");
		dao.update("com.sdd.mapper.invest.PostOrderMapper.updateStatus", orderParams);
		//根据交单用户修改账号可提现余额以及待提现余额
		dao.update("com.sdd.mapper.invest.AccountMapper.updatePass", params);
		//更新余额详细记录
		Map<String, Object> recordParams = new HashMap<String, Object>();
		recordParams.put("type", "2");
		recordParams.put("remark", "交单<审核通过:" + cashBack + ">");
		recordParams.put("account", account);
		recordParams.put("user", pd.get("user"));
		dao.update("com.sdd.mapper.invest.PostOrderMapper.updateBalanceList", recordParams);
		return Constants.SUCCESS;
	}
	
	/**
	 * 审核失败
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public String passErrorService(PageData pd) throws Exception {
		//查询交单数据(判断状态)
		Object object = dao.findForObject("com.sdd.mapper.invest.PostOrderMapper.listById", pd);
		if(object == null){
			return "交单记录不存在!";
		}
		Map<String, Object> order = Tools.objToMap(object);
		String status = Tools.getStringValue(order.get("status"));
		if(!"2".equals(status)){
			return "只能处理<<审核中>>状态的数据!";
		}
		String account = Tools.getStringValue(order.get("account"));
		//cash_back
		Double cashBack = order.get("cash_back") == null ? 0 : Double.valueOf(order.get("cash_back").toString());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("cash_back", cashBack);
		//判断用户待提现余额是否足够,不足够需要插入预警表
		int count = (int) dao.findForObject("com.sdd.mapper.invest.AccountMapper.isPass", params);
		if(count != 1){
			Map<String, Object> warnMap = new HashMap<String, Object>();
			warnMap.put("account", account);
			warnMap.put("remark", "账号异常<待返现余额不足>,请查明原因!");
			dao.save("com.sdd.mapper.invest.PostOrderMapper.insertWarn", warnMap);
			return "账号异常<待返现余额不足>,请查明原因!";
		}
		//修改交单状态为(2异常单)
		Map<String, Object> orderParams = new HashMap<String, Object>();
		orderParams.put("ID", pd.getString("ID"));
		orderParams.put("status", "3");
		orderParams.put("remark", pd.get("remark"));
		dao.update("com.sdd.mapper.invest.PostOrderMapper.updateStatus", orderParams);
		//根据交单用户修改账号待提现余额
		dao.update("com.sdd.mapper.invest.AccountMapper.updateNoPass", params);
		//更新余额详细记录
		Map<String, Object> recordParams = new HashMap<String, Object>();
		recordParams.put("type", "2");
		recordParams.put("remark", "交单<"+pd.get("remark")+":"+cashBack+">");
		recordParams.put("account", account);
		recordParams.put("user", pd.get("user"));
		dao.update("com.sdd.mapper.invest.PostOrderMapper.updateBalanceList", recordParams);
		return Constants.SUCCESS;
	}
	
	/*
	 * 修改页面数据查询
	 */
	public Map<String, Object> editService(PageData pd) throws Exception {
		Object object = dao.findForObject("com.sdd.mapper.invest.PostOrderMapper.listById", pd);
		return Tools.objToMap(object);
	}
	
	/*
	 * 修改
	 */
	public String updateService(PageData pd) throws Exception {
		int count = (int) dao.findForObject("com.sdd.mapper.invest.PostOrderMapper.isExists", pd);
		if(count > 0){
			dao.update("com.sdd.mapper.invest.PostOrderMapper.update", pd);
			return Constants.SUCCESS;
		}else{
			return "记录已被其他用户删除!";		
		}
	}
	
	/*
	 * 删除
	 */
	public String delService(PageData pd) throws Exception {
		dao.delete("com.sdd.mapper.invest.PostOrderMapper.delete", pd);
		return Constants.SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> reportData() throws Exception {
		return (Map<String, Object>) dao.findForObject("com.sdd.mapper.invest.PostOrderMapper.reportData", "");
	}
	
}

