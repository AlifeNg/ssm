<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2017/3/28
  Time: 13:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body style="background-color: ${user.theme}">

<form action="changeTheme" method="post">
    <input type="hidden" name="id" value="${user.id}"><br>
    <select name="theme">
        <option value="white">白色</option>
        <option value="red">红色</option>
        <option value="blue">蓝色</option>
        <option value="yellow">黄色</option>
    </select>
    <input type="submit" value="修改">
</form>

${msg}
</body>
</html>
