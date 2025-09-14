<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>申請編集</title></head>
<body>
  <h2>有給申請 編集</h2>
  <c:if test="${not empty errorMessage}">
    <p style="color:red">${errorMessage}</p>
  </c:if>
  <form action="${pageContext.request.contextPath}/paidleave/edit" method="post">
    <input type="hidden" name="id" value="${dto.id}"/>
    <label>開始日:
      <input type="date" name="startDate" 
             value="${dto.startDate}" required/>
    </label><br/>
    <label>終了日:
      <input type="date" name="endDate" 
             value="${dto.endDate}" required/>
    </label><br/>
    <label>理由:<br/>
      <textarea name="reason" rows="4" cols="40" required>
${dto.reason}</textarea>
    </label><br/>
    <button type="submit">更新</button>
  </form>
  <p><a href="${pageContext.request.contextPath}/paidleave/list">一覧に戻る</a></p>
</body>
</html>
