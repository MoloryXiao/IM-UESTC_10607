<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>找回密码</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta name="Keywords" content="网站关键词">
    <meta name="Description" content="网站描述">
    <link rel="stylesheet" href="./css/base.css">
    <link rel="stylesheet" href="./css/iconfont.css">
    <link rel="stylesheet" href="./css/reg.css">
</head>
<body>
<div id="ajax-hook"></div>
<div class="wrap">
    <div class="wpn">
        <div class="form-data find_password">
        	<form action="vCode" name="recvVCode" method="POST">
	            <h4>找回密码</h4>
	            <p class="p-input pos">
	                <label for="pc_tel">请输入验证码</label>
	                <input type="text" name="vcodetext" id="pc_tel">
	             </p>
	            <button class="lang-btn next" onclick="return checkVerifyCode()">下一步</button>
	       	</form>
            <p class="right">Powered by ZiQin© 2018</p>
        </div>
    </div>
</div>
<script src="./js/jquery.js"></script>
<script src="./js/agree.js"></script>
<script src="./js/reset.js"></script>
<script src="./js/checkInput.js"></script>
</body>
</html>