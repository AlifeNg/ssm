<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2017/3/28
  Time: 15:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<select id="theme">
    <option value="white">白色</option>
    <option value="red">红色</option>
    <option value="blue">蓝色</option>
    <option value="yellow">黄色</option>
</select>
<input type="button" onclick="changeTheme()" value="修改">
</body>
<script>
    function changeTheme() {
        var obj = document.getElementById(theme);
        var index = obj.selectedIndex; // 选中索引

        var text = obj.options[index].text; // 选中文本

        var value = obj.options[index].value; // 选中值
        alert(value);
    }

</script>
</html>
