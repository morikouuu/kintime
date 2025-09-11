<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>有給申請画面</title></head>
<body>
  <h2>有給申請</h2>
  <c:if test="${not empty errorMessage}">
  <p class="error-message">${errorMessage}</p>
</c:if>
  <form action="${pageContext.request.contextPath}/paidleave/apply" method="post">
    <!-- ユーザー名入力は不要。サーバ側でセッションから取得します -->
    開始日: <input type="date" name="startDate" required><br>
    終了日: <input type="date" name="endDate" required><br>
    理由:   <input type="text" name="reason" required><br>
    <button type="submit">申請</button>
    <a href="${pageContext.request.contextPath}/attendance">戻る</a>
  </form>
</body>
</html>