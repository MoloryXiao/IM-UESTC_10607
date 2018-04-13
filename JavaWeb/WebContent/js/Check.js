function checkSubmit()
{
	if (checkNickname() == false || checkPasswd() == false || checkPhoneNumber() == false) {
		checkPasswd();
		checkPhoneNumber();
		alert("基本信息有误");
		return false; 
	}
}

function checkNickname()
{
	var x = document.forms["userInfo"]["nickname"].value;
	if (x == null || x == "") {
		tip("昵称不能为空", "p_name");
		return false;
	}
	else {
		tip("", "p_name");
	}
}

function checkPasswd()
{
	var x = document.forms["userInfo"]["password"].value;
	var str = new RegExp(/^(?![^a-zA-Z]+$)(?!\D+$)/);
	if (x == null || x == "") {
		tip("密码不能为空", "p_passwd");
		return false;
	}
	else if (!str.test(x)) {
		tip("密码需要包括字母和数字", "p_passwd");
		return false;
	}
	else {
		tip("", "p_passwd");
	}
}

function checkPhoneNumber() 
{
	var x = document.forms["userInfo"]["phoneNumber"].value;
	var str = new RegExp(/^[1][3,4,5,7,8][0-9]{9}$/);
	if (x == null || x == "") {
		tip("手机号码不能为空", "p_phoneNumber");
		return false;
	}
	else if (!str.test(x)) {
		tip("手机号码不符合规范", "p_phoneNumber");
		return false;
	}
	else {
		tip("", "p_phoneNumber");
	}
}

function tip(text, id) {
	document.getElementById(id).innerHTML = text;
}