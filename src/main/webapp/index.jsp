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
    <link id="themeId" rel="stylesheet" type="text/css" href="css/white.css">
    <script src="js/jquery.min.js" ></script>
    <script src="js/style.js"></script>
</head>
<body>
<select id="theme">
    <option value="white">白色</option>
    <option value="red">红色</option>
    <option value="yellow">黄色</option>
</select>
<input type="button" onclick="changeTheme()" value="修改">
</body>
<script>

    function changeTheme() {
        var theme = $('#theme').val();
        var Days = 30*12;   //cookie 将被保存一年
        var exp  = new Date();  //获得当前时间
        exp.setTime(exp.getTime() + Days*24*60*60*1000);  //换成毫秒
        loadCss(themeStyle[theme]);
        document.cookie = "theme=" + theme + ";expires=" + exp.toGMTString();
    }


    
</script>
</html>
