<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>FCM Push</title>
</head>
<body>
<h2>FCM 알림 메시지 입력</h2>
<c:if test="${not empty success }">
	<span style="color:red;font-size:1.8em">${success}</span>개의 앱에 메시지가 전송 되었습니다
</c:if>
<form action="<c:url value="/message"/>" method="post">
	<table style="border-spacing:1px;background-color:gray;width:400px">
		<tr style="border-spacing:1px;background-color:white">
			<td style="width:20%">제목</td>
			<td><input type="text" name="dataTitle" style="width:90%"/></td>
		</tr>
		<tr style="border-spacing:1px;background-color:white">
			<td colspan="2">메시지</td>
			
		</tr>
		<tr style="border-spacing:1px;background-color:white">
			
			<td colspan="2"><textarea name="dataBody" style="width:90%;height:200px" ></textarea></td>
		</tr>
		<tr style="border-spacing:1px;background-color:white">
			<td colspan="2" style="text-align:center">
			<input type="submit" value="확인"/></td>			
		</tr>  
		
	</table>
</form>
</body>
</html>


