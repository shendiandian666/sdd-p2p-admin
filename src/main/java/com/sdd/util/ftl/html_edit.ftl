<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org"
	xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

<head>
<#if hasSelect == 'true'>
<!-- 下拉框 -->
<link rel="stylesheet" th:href="@{/ace/css/chosen.css}"/>
</#if>
<dif th:include="system/index/top::tophead"></dif>
</head>
<body class="no-skin">
	<!-- /section:basics/navbar.layout -->
	<div class="main-container" id="main-container">
		<!-- /section:basics/sidebar -->
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<div class="page-header">
							<h1>
								<small>
									<i class="ace-icon fa fa-angle-double-right"></i>
									编辑
								</small>
							</h1>
					</div><!-- /.page-header -->
					<div class="row">
						<div class="col-xs-8 col-xs-offset-2">

							<form th:action="@{'/${model}/${class?lower_case}'+${r"${action}"}}" name="Form" id="Form"
								method="post">
								<input type="hidden" name="${primaryKey}" id="${primaryKey}" th:value="${r"${"}${class?lower_case} == null ? '' : ${class?lower_case}.${primaryKey}}" />
								<div id="zhongxin" style="padding-top: 13px;">
									<table id="table_report"
										class="table table-striped table-bordered table-hover">
										
										<#list columnMap?keys as key>  
										<#if columnMap[key][1] == 'true'>
										<tr>
											<td style="text-align: right; padding-top: 13px;">${columnMap['${key}'][0]}:</td>
											<td>
											<select
												class="chosen-select form-control" name="${key}"
												id="${key}" data-placeholder="${columnMap['${key}'][0]}"
												style="vertical-align: top;">
													<option value=""></option>
													<option th:each="item,status : ${r"${"}${key?lower_case?replace("_","")}${r"List}"}" th:selected="${r"${"}item.BIANMA == (${class?lower_case} == null ? '' : ${class?lower_case}.${key})${r"}"}" th:value="${r"${item.BIANMA}"}" th:inline="text">[[${r"${item.NAME}"}]]</option>
												</select>
											</td>
										</tr>
										<#else>
										<tr>
											<td style="text-align: right; padding-top: 13px;">${columnMap['${key}'][0]}:</td>
											<td><input type="text" name="${key}" id="${key}"
												th:value="${r"${"}${class?lower_case} == null ? '' : ${class?lower_case}.${key}}" style="width: 98%;" /></td>
										</tr>
										</#if>
										</#list>
									</table>
								</div>
								<div id="zhongxin2" class="center" style="display: none">
									<br />
									<br />
									<br />
									<br />
									<br />
									<img th:src="@{/images/jiazai.gif}" /><br />
									<h4 class="lighter block green">提交中...</h4>
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
	</div>
	<!-- /.main-container -->

	<footer>
		<div style="width: 100%; padding-bottom: 2px;" class="center">
			<a class="btn btn-mini btn-primary" onclick="save();">保存</a> <a
				class="btn btn-mini btn-danger" onclick="cancel();">取消</a>
		</div>
	</footer>

	<!-- 页面底部js¨ -->
	<div th:include="system/index/foot::footdiv" />
	<#if hasSelect == 'true'>
	<!-- 下拉框 -->
	<script th:src="@{/ace/js/chosen.jquery.js}"></script>
	</#if>
	<!--提示框-->
	<script type="text/javascript" th:src="@{/js/jquery.tips.js}"></script>
	<script type="text/javascript" th:inline="javascript">
		$(top.hangge());
		var action = [[${r"${action}"}]];		
		
		function cancel(){
			/*<![CDATA[*/
			top.jzts();
			if(true && document.forms[0]){
				var url = /*[[${r"${url}"}]]*/;
				document.forms[0].action = url;
				document.forms[0].submit();
			}
			/*]]>*/		
		}
		
		//保存
		function save() {
			<#list columnMap?keys as key>  
			<#if columnMap[key][1] == 'false'>
			if ($("#${key}").val() == "") {
				$("#${key}").tips({
					side : 3,
					msg : '请输入${columnMap[key][0]}',
					bg : '#AE81FF',
					time : 2
				});
				$("#${key}").focus();
				return false;
			}
			</#if>
			</#list>
			$.post(/*[[@{'/${model}/${class?lower_case}'+${r"${action}"}}]]*/,$("#Form").serializeJSON(),function(data){
				if(data == 'SUCCESS'){
					cancel();
				}else{
					alert(data);
				}
			});
		}
	<#if hasSelect == 'true'>
		$(function(){
			//下拉框
			if (!ace.vars['touch']) {
				$('.chosen-select').chosen({
					allow_single_deselect : true
				});
				$(window).off('resize.chosen').on('resize.chosen', function() {
					$('.chosen-select').each(function() {
						var $this = $(this);
						$this.next().css({
							'width' : $this.parent().width()
						});
					});
				}).trigger('resize.chosen');
				$(document).on('settings.ace.chosen',
						function(e, event_name, event_val) {
							if (event_name != 'sidebar_collapsed')
								return;
							$('.chosen-select').each(function() {
								var $this = $(this);
								$this.next().css({
									'width' : $this.parent().width()
								});
							});
						});
				$('#chosen-multiple-style .btn').on(
						'click',
						function(e) {
							var target = $(this).find('input[type=radio]');
							var which = parseInt(target.val());
							if (which == 2)
								$('#form-field-select-4').addClass(
										'tag-input-style');
							else
								$('#form-field-select-4').removeClass(
										'tag-input-style');
						});
			}
		});
	</#if>
	</script>
</body>
</html>