<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>有給申請画面</title></head>
<body>
  <h2>有給申請</h2>
  <form action="${pageContext.request.contextPath}/paidleave/apply" method="post">
    <!-- ユーザー名入力は不要。サーバ側でセッションから取得します -->
    開始日: <input type="date" name="startDate" required><br>
    終了日: <input type="date" name="endDate" required><br>
    理由:   <input type="text" name="reason" required><br>
    <button type="submit">申請</button>
  </form>
</body>
</html>