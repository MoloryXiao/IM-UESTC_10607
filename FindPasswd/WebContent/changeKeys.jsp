<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>修改密码</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta name="Keywords" content="网站关键词">
    <meta name="Description" content="网站描述">
    <link rel="stylesheet" href="./css/base.css">
    <link rel="stylesheet" href="./css/iconfont.css">
    <link rel="stylesheet" href="./css/reg.css">
</head>
<body>
    <div id="ajax-hook"></div>
    <div class="wrap">
        <div class="wpn">
            <div class="form-data pos">
                <a href=""><img src="./img/logo.png" class="head-logo"></a>
                <!--<p class="tel-warn hide"><i class="icon-warn"></i></p>-->
                <form action="change" name="changeKey" method="POST">
                    <p class="p-input pos">
                        <label for="tel">请输入密码</label>
                        <input type="password" name="key" id="passwd1" onBlur="inputPasswd1()" autocomplete="off">
                        <span class="tel-warn tel-err hide"><em></em><i class="icon-warn"></i></span>
                    </p>
					<p class="p-message pos" id="p_passwd1">&nbsp</p>
                    <p class="p-input pos">
                        <label for="tel">请再次输入密码</label>
                        <input type="password" name="keyAgain" id="passwd2" onBlur="inputPasswd2()" autocomplete="off">
                        <span class="tel-warn tel-err hide"><em></em><i class="icon-warn"></i></span>
                    </p>
					<p class="p-message pos" id="p_passwd2">&nbsp</p>
                	<button class="lang-btn" onclick="return checkInputSame()">更改密码</button>
                </form>
            </div>
        </div>
    </div>
    <script src="./js/jquery.js"></script>
    <script src="./js/agree.js"></script>
    <script src="./js/reg.js"></script>
    <script src="./js/checkInput.js"></script>
</body>
</html>