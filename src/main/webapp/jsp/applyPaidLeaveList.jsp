<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>管理者用有給一覧</title></head>
<body>
  <h2>有給申請一覧</h2>
  <table border="1">
    <tr>
      <th>ID</th><th>ユーザー</th><th>開始日</th><th>終了日</th>
      <th>理由</th><th>ステータス</th><th>操作</th>
    </tr>
    <c:forEach var="req" items="${requests}">
      <tr>
        <td>${req.id}</td>
        <td>${req.username}</td>
        <td>${req.startDate}</td>
        <td>${req.endDate}</td>
        <td>${req.reason}</td>
        <td>${req.status}</td>
        <td>
          <c:if test="${req.status == 'PENDING'}">
            <form action="${pageContext.request.contextPath}/paidleave/admin/approve"
                  method="post" style="display:inline">
              <input type="hidden" name="id" value="${req.id}">
              <button name="action" value="approve">承認</button>
            </form>
            <form action="${pageContext.request.contextPath}/paidleave/admin/approve"
                  method="post" style="display:inline">
              <input type="hidden" name="id" value="${req.id}">
              <button name="action" value="reject">却下</button>
            </form>
          </c:if>
        </td>
      </tr>
    </c:forEach>
  </table>
  <a href="${pageContext.request.contextPath}/jsp/admin_menu.jsp">戻る</a>
</body>
</html>
