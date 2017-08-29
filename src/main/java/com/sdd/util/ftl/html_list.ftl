<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org"
	xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3"
	xmlns:shiro="http://www.pollix.at/thymeleaf/shiro">

<head>
<!-- 下拉框 -->
<link rel="stylesheet" th:href="@{/ace/css/chosen.css}" />
<!-- 日期框 -->
<link rel="stylesheet" th:href="@{/ace/css/datepicker.css}" />
<dif th:include="system/index/top::tophead"></dif>
</head>
<body class="no-skin">

	<!-- /section:basics/navbar.layout -->
	<div class="main-container" id="main-container">
		<!-- /section:basics/sidebar -->
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">

							<!-- 检索  -->
							<form th:action="@{/${model}/${class?lower_case}List}" method="post" name="Form"
								id="Form">
								<table style="margin-top: 5px;">
									<tr>
										<td>
											<div class="nav-search">
												<span class="input-icon"> <input type="text"
													placeholder="" class="nav-search-input"
													id="nav-search-input" autocomplete="off" name="keywords"
													th:value="${r"${pd.keywords }"}" /> <i
													class="ace-icon fa fa-search nav-search-icon"></i>
												</span>
											</div>
										</td>
										<td style="vertical-align: top; padding-left: 2px"><a
											class="btn btn-light btn-xs" onclick="tosearch();" title="检索"><i
												id="nav-search-icon"
												class="ace-icon fa fa-search bigger-110 nav-search-icon blue"></i></a></td>
									</tr>
								</table>
								<!-- 检索  -->

								<table id="simple-table"
									class="table table-striped table-bordered table-hover"
									style="margin-top: 5px;">
									<thead>
										<tr>
											<th class="center" style="width: 50px;">序号</th>
											<#list columnMap?keys as key>  
											<th class="center">${columnMap['${key}'][0]}</th>
											</#list>
											<th class="center">操作</th>
										</tr>
									</thead>

									<tbody>


										<!-- 开始循环 -->
										<tr th:each="item,status : ${r"${"}${class?lower_case}${r"List}"}" th:inline="text">
											<td class='center' style="width: 30px;">[[${r"${status.index+1}"}]]</td>
											<#list columnMap?keys as key>  
											<#if columnMap[key][1] == 'true'>
											<td class="center" th:text="${r"${item."}${key}${r"_DESC}"}"></td>
											<#else>
											<td class="center" th:text="${r"${item."}${key}${r"}"}"></td>
											</#if>
											</#list>
											<td class="center">
												<div class="inline pos-rel">
													<button class="btn btn-minier btn-yellow dropdown-toggle" data-toggle="dropdown" data-position="auto">
														<i class="ace-icon fa fa-caret-down icon-only bigger-120"></i>
													</button>
													<ul
														class="dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close">
														<li shiro:hasPermission="${model}/${class?lower_case}:detail"><a style="cursor: pointer;"
															th:onclick="'detail(\''+${r"${item."}${primaryKey}${r"}"}+'\');'"
															class="tooltip-success" data-rel="tooltip" title="详情">
																<span class="blue"> <i
																	class="ace-icon fa fa-eye bigger-120"></i>
															</span>
														</a></li>
														<li shiro:hasPermission="${model}/${class?lower_case}:edit"><a style="cursor: pointer;"
															th:onclick="'edit(\''+${r"${item."}${primaryKey}${r"}"}+'\');'"
															class="tooltip-success" data-rel="tooltip" title="修改">
																<span class="green"> <i
																	class="ace-icon fa fa-pencil-square-o bigger-120"></i>
															</span>
														</a></li>
														<li shiro:hasPermission="${model}/${class?lower_case}:delete"><a style="cursor: pointer;"
															th:onclick="'del(\''+@{/${model}/${class?lower_case}Del(${primaryKey}=${r"${item."}${primaryKey}${r"}"})}+'\');'"
															class="tooltip-error" data-rel="tooltip" title="删除">
																<span class="red"> <i
																	class="ace-icon fa fa-trash-o bigger-120"></i>
															</span>
														</a></li>
													</ul>
												</div>
											</td>
										</tr>
									</tbody>
								</table>
								<div class="page-header position-relative">
									<table style="width: 100%;">
										<tr>
											<td style="vertical-align: top;"><a shiro:hasPermission="${model}/${class?lower_case}:add"
												class="btn btn-mini btn-success" onclick="add();">新增</a></td>
											<td style="vertical-align: top;"><div class="pagination"
													style="float: right; padding-top: 0px; margin-top: 0px;"
													th:include="system/page::pagination"></div></td>
										</tr>
									</table>
								</div>
							</form>

						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- /.page-content -->
			</div>
		</div>
		<!-- /.main-content -->

		<!-- 返回顶部 -->
		<a href="#" id="btn-scroll-up"
			class="btn-scroll-up btn btn-sm btn-inverse"> <i
			class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>

	</div>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<div th:include="system/index/foot::footdiv" />
	<!-- 删除时确认窗口 -->
	<script th:src="@{/ace/js/bootbox.js}"></script>
	<!-- ace scripts -->
	<script th:src="@{/ace/js/ace/ace.js}"></script>
	<!-- 下拉框 -->
	<script th:src="@{/ace/js/chosen.jquery.js}"></script>
	<!-- 日期框 -->
	<script th:src="@{/ace/js/date-time/bootstrap-datepicker.js}"></script>
	<!--提示框-->
	<script type="text/javascript" th:src="@{/js/jquery.tips.js}"></script>
	<script type="text/javascript" th:inline="javascript">
		$(top.hangge());//关闭加载状态
		//检索
		function tosearch(){
			top.jzts();
			$("#Form").submit();
		}
		function add() {
			/*<![CDATA[*/
			top.jzts();
			if(true && document.forms[0]){
				var url = [[@{/${model}/${class?lower_case}Add(url=@{/${model}/${class?lower_case}List(pageNum=${r"${page.pageNum}"},pageSize=${r"${page.pageSize}"})})}]];
				document.forms[0].action = url;
				document.forms[0].submit();
			}
			/*]]>*/		
		}
		function edit(${primaryKey}) {
			/*<![CDATA[*/
			top.jzts();
			if(true && document.forms[0]){
				var url = [[@{/${model}/${class?lower_case}Edit(url=@{/${model}/${class?lower_case}List(pageNum=${r"${page.pageNum}"},pageSize=${r"${page.pageSize}"})})}]];
				url += "&${primaryKey}=" + ${primaryKey};
				document.forms[0].action = url;
				document.forms[0].submit();
			}
			/*]]>*/	
		}
		function detail(${primaryKey}) {
			/*<![CDATA[*/
			top.jzts();
			if(true && document.forms[0]){
				var url = [[@{/${model}/${class?lower_case}Detail(url=@{/${model}/${class?lower_case}List(pageNum=${r"${page.pageNum}"},pageSize=${r"${page.pageSize}"})})}]];
				url += "&${primaryKey}=" + ${primaryKey};
				document.forms[0].action = url;
				document.forms[0].submit();
			}
			/*]]>*/	
		}
		function del(url){
			bootbox.confirm("确定要删除吗?", function(result) {
				if(result) {
					top.jzts();
					$.get(url,function(data){
						if(data == 'SUCCESS'){
							nextPage(/*[[${r"${page.pageNum}"}]]*/);
						}else{
							alert(data);
						}
					});
				}
			});
		}
		
	</script>


</body>
</html>