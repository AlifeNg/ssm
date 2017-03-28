/**
 * Created by admin on 2017/3/28.
 */
var themeStyle = {
    yellow : "css/yellow.css",
    red : "css/red.css",
    white : "css/white.css"
}

$(function () {
    var theme=document.cookie.split(";")[0].split("=")[1];
    loadCss(themeStyle[theme]);
})

function loadCss(url) {
    $('#themeId').attr("href", url);
}