<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>有給申請一覧</title></head>
<body>
  <h2>申請中の有給一覧</h2>
  <table border="1">
    <tr><th>ID</th><th>開始日</th><th>終了日</th><th>理由</th><th>操作</th></tr>
    <c:forEach var="d" items="${list}">
      <tr>
        <td>${d.id}</td>
        <td>${d.startDate}</td>
        <td>${d.endDate}</td>
        <td><c:out value="${d.reason}"/></td>
        <td>
          <a href="${pageContext.request.contextPath}/paidleave/edit?id=${d.id}">編集</a>
          <form action="${pageContext.request.contextPath}/paidleave/delete" 
                method="post" style="display:inline"
                onsubmit="return confirm('削除しますか？');">
            <input type="hidden" name="id" value="${d.id}"/>
            <button type="submit">削除</button>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
  <p><a href="${pageContext.request.contextPath}/paidleave/apply">新規申請</a></p>
  <p><a href="${pageContext.request.contextPath}/attendance">戻る</a></p>
  
</body>
</html>
