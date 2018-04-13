function checkInputSame()
{
	var passwd = document.forms["changeKey"]["key"].value;
	var passwd2 = document.forms["changeKey"]["keyAgain"].value;
	if (isEmpty(passwd, "p_passwd1") == true || isEmpty(passwd2, "p_passwd2") == true) {
		isEmpty(passwd2, "p_passwd2");
		return false;
	}
	if (isStandard(passwd, "p_passwd1") == false || isStandard(passwd2, "p_passwd2") == false) {
		isStandard(passwd2, "p_passwd2");
		return false;
	}
	if (passwd != passwd2) {
		alert("两边密码不相等");
		tip("&nbsp", "p_passwd1");
		tip("&nbsp", "p_passwd2");
		return false;
	}
}

function isEmptyForID() 
{
	var KIMId = document.forms["findPasswd"]["id"].value;
	var str = new RegExp(/^[1-9][0-9]{4}$/);
	if (KIMId == "" || KIMId == null) {
		alert("帐号不能为空");
		return false;
	}
	else if (!str.test(KIMId)) {
		alert("帐号不符合规范");
		return false;
	}
}

function checkVerifyCode()
{
	var vcode = document.forms["recvVCode"]["vcodetext"].value;
	var str = new RegExp(/^[0-9]{6}$/);
	if (vcode == "" || vcode == null) {
		alert("验证码不能为空");
		return false;
	}
	else if (!str.test(vcode)) {
		alert("验证码必须为6位");
		return false;
	}
}

function isEmpty(passwd, id)
{
	if (passwd == "" || passwd == null) {
		tip("密码不能为空", id);
		return true;
	}
	else {
		tip(" ", id);
		return false;
	}
}

function isStandard(passwd, id)
{
	var str = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$)/);
	if (!str.test(passwd)) {
		tip("密码需要包括字母和数字", id);
		return false;
	}
	else {
		tip(" ", id);
		return true;
	}
}

function inputPasswd1()
{
	var passwd = document.forms["changeKey"]["key"].value;
	var str = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$)/);
	if (isEmpty(passwd, "p_passwd1")) {
		return false;
	}
	else if (!isStandard(passwd, "p_passwd1")) {
		return false;
	}
	else {
		tip("&nbsp", "p_passwd1");
	}
}

function inputPasswd2()
{
	var passwd = document.forms["changeKey"]["keyAgain"].value;
	var str = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$)/);
	if (isEmpty(passwd, "p_passwd2")) {
		return false;
	}
	else if (!isStandard(passwd, "p_passwd2")) {
		return false;
	}
	else {
		tip("&nbsp", "p_passwd2");
	}
}

function tip(text, id) {
	document.getElementById(id).innerHTML = text;
}