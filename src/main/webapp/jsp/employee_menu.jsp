<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>従業員メニュー</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/style.css">
<style>
.container {
	max-width: 800px;
	margin: 20px auto;
}

.button-group {
	margin: 10px 0;
}

table {
	width: 100%;
	border-collapse: collapse;
}

th, td {
	border: 1px solid #ccc;
	padding: 8px;
	text-align: center;
}

.success-message {
	color: green;
}

.button {
	padding: 6px 12px;
	text-decoration: none;
	border: 1px solid #333;
	background-color: #eee;
	margin-right: 5px;
}

.button.secondary {
	background-color: #ccc;
}
</style>
</head>
<body>
	<div class="container">
		<h1>従業員メニュー</h1>
		<p>ようこそ, ${user.username}さん</p>

		<c:if test="${not empty notifications}">
			<div class="notifications">
				<h3>お知らせ</h3>
				<ul>
					<c:forEach var="msg" items="${notifications}">
						<li><c:out value="${msg}" /></li>
					</c:forEach>
				</ul>
			</div>
		</c:if>

		<c:if test="${not empty sessionScope.successMessage}">
			<p class="success-message">
				<c:out value="${sessionScope.successMessage}" />
			</p>
			<c:remove var="successMessage" scope="session" />
		</c:if>

		<div class="button-group">
			<form action="${pageContext.request.contextPath}/attendance"
				method="post" style="display: inline;">
				<input type="hidden" name="action" value="check_in"> <input
					type="submit" value="出勤">
			</form>
			<form action="${pageContext.request.contextPath}/attendance"
				method="post" style="display: inline;">
				<input type="hidden" name="action" value="check_out"> <input
					type="submit" value="退勤">
			</form>
		</div>

		<h2>あなたの勤怠履歴</h2>
		<table>
			<thead>
				<tr>
					<th>出勤時刻</th>
					<th>退勤時刻</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="att" items="${attendanceRecords}">
					<tr>
						<td><c:out value="${att.checkInTime}" /></td>
						<td><c:out value="${att.checkOutTime}" /></td>
					</tr>
				</c:forEach>
				<c:if test="${empty attendanceRecords}">
					<tr>
						<td colspan="2">勤怠記録がありません。</td>
					</tr>
				</c:if>
			</tbody>
		</table>

		<div class="button-group">
			<a href="${pageContext.request.contextPath}/logout"
				class="button secondary">ログアウト</a>
		</div>
		<div class="button-group">
			<a href="${pageContext.request.contextPath}/paidleave/apply"
				class="button"> 有給申請 </a> <a
				href="${pageContext.request.contextPath}/paidleave/list"
				class="button"> 有給申請一覧 </a>
		</div>
	</div>
</body>
</html>