<%@ page language="java"
	import="java.util.*,com.sunyard.insurance.common.GlobalVar,com.sunyard.insurance.entity.TRM_TASK_CONFIG"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	//影像到达通知
	StringBuffer infromStr = new StringBuffer();
	
	Iterator it = GlobalVar.informAppMap.entrySet().iterator();    
	while (it.hasNext()) {    
	        Map.Entry pairs = (Map.Entry)it.next();   
	        infromStr.append(pairs.getKey() + ",");
	}
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">
		<title>TRM缓存</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
	</head>
	
	<body>
		<h4> 
			传输缓存V2.0.2&nbsp; 当前服务器节点信息 
		</h4>
		<table border="1">
			<tr>
				<td align="left">
					Svr_Id
				</td>
				<td align="left"><%=GlobalVar.serverId%></td>
				<td align="left">
					缓存节点标识
				</td>
			</tr>
			<tr>
				<td align="left">
					Svr_Vip
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getOrgVip()%></td>
				<td align="left">
					缓存节点虚拟IP
				</td>
			</tr>
			<tr>
				<td align="left">
					Svr_Ip
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getSvrIp()%></td>
				<td align="left">
					缓存节点物理IP
				</td>
			</tr>
			<tr>
				<td align="left">
					SvrLevel
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getSvrLevel()%></td>
				<td align="left">
					缓存节点等级
				</td>
			</tr>
			<tr>
				<td align="left">
					SvrName
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getSvrName()%></td>
				<td align="left">
					缓存节点名称
				</td>
			</tr>
			<tr>
				<td align="left">
					OrgCode
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getOrgCode()%></td>
				<td align="left">
					缓存节点机构代码
				</td>
			</tr>
			<tr>
				<td align="left">
					SaveInCM
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getSaveInCM()%></td>
				<td align="left">
					缓存存储标识(0:缓存至本地&nbsp;&nbsp;1:缓存至CM)
				</td>
			</tr>
			<tr>
				<td align="left">
					Http_port
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getHttp_port()%></td>
				<td align="left">
					缓存节点HTTP端口号
				</td>
			</tr>
			<tr>
				<td align="left">
					Socket_port
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getSocket_port()%></td>
				<td align="left">
					缓存节点Socket端口号
				</td>
			</tr>
			<tr>
				<td align="left">
					SaveType
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getSaveType()%></td>
				<td align="left">
					缓存批次存储模式(1:多个版本相同一个存储目录2:不同版本不同存储目录)
				</td>
			</tr>
			<tr>
				<td align="left">
					DateType
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getDateType()%></td>
				<td align="left">
					存储目录日期格式(1:YYYY/MM/DD&nbsp;&nbsp;2:YYYY/MMDD)
				</td>
			</tr>
			<tr>
				<td align="left">
					HashRand
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getHashRand()%></td>
				<td align="left">
					存储目录随机目录层数，每层最多99个文件夹
				</td>
			</tr>
			<tr>
				<td align="left">
					RootPath
				</td>
				<td align="left"><%=GlobalVar.svrInfoBean.getRootPath()%></td>
				<td align="left">
					批次存储根目录
				</td>
			</tr>
			<tr>
				<td align="left">
					tempDir
				</td>
				<td align="left"><%=GlobalVar.tempDir%></td>
				<td align="left">
					缓存临时文件夹
				</td>
			</tr>
			<tr>
				<td align="left">
					USMURL
				</td>
				<td align="left"><%=GlobalVar.USMURL%></td>
				<td align="left">
					SunUSM请求地址
				</td>
			</tr>
		</table>
		<h4>
			影像到达通知配置
		</h4>
		<table border="1">
			<tr>
				<td align="left">
					业务类型
				</td>
				<td align="left"><%=infromStr.toString()%></td>
				<td align="left">
					需要到达通知的业务类型
				</td>
			</tr>
		</table>

		<h4>
			排程信息
		</h4>
		<table border="1">
			<tr>
				<td align="left">
					执行SVR_ID
				</td>
				<td align="left">
					排程类型
				</td>
				<td align="left">
					开始时间
				</td>
				<td align="left">
					结束时间
				</td>
			</tr>
			<%
				for (int i = 0; i < GlobalVar.taskList.size(); i++) {
					TRM_TASK_CONFIG taskConfig = (TRM_TASK_CONFIG)GlobalVar.taskList.get(i);
					String taskType = "";
					if(taskConfig.getTask_type().equals("1")) {
						taskType = "迁移";
					} else if(taskConfig.getTask_type().equals("2")) {
						taskType = "推送";
					} else if(taskConfig.getTask_type().equals("3")) {
						taskType = "清理";
					}
			%>
			<tr>
				<td align="left">
					<%=taskConfig.getSvr_id() %>
				</td>
				<td align="left">
					<%=taskType %>
				</td>
				<td align="left">
					<%=taskConfig.getStart_time() %>
				</td>
				<td align="left">
					<%=taskConfig.getEnd_time() %>
				</td>
			</tr>
			<% } %>
		</table>
	</body>
</html>
