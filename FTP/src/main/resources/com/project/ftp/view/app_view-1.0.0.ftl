<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="description" content="FTP Application description"/>
    <meta name="keywords" content="FTP Application, File upload, PDF, JPEG, JPG, PNG">
    <meta name="author" content="FTP Application Author">
    <title>FTP Application</title>
    <link rel="stylesheet" type="text/css" href="/assets/static/libs/bootstrap-v4.4.1.css">
    <link rel="stylesheet" type="text/css" href="/assets/static/dist-ftp-app/style.css?v=${appVersion}">
</head>
<body class="theme-grey">
<noscript>You need to enable JavaScript to run this app.</noscript>
<input type="hidden" style="display: none;" name="pageData" id="pageData"
       value="page=${pageName},app_version=${appVersion},is_login=${isLogin},username=${userName},user_display_name=${userDisplayName},is_login_user_admin=${isLoginUserAdmin},upload_file_api_version=${uploadFileApiVersion},is_guest_enable=${isGuestEnable},is_forgot_password_enable=${isForgotPasswordEnable}"/>
<div id="root"></div>
<script type="text/javascript" src="/assets/static/libs/jquery-2.1.3.js"></script>
<script type="text/javascript">
var GLOBAL = {
    baseapi: "",
    basepathname: "",
    JQ: $,
    isReactEnv: false
};
GLOBAL.currentPageData = document.getElementById("pageData").value;
window.GLOBAL = GLOBAL;
</script>

<script type="text/javascript" src="/assets/static/dist-ftp-app/script1.js?v=${appVersion}"></script>
<script type="text/javascript" src="/assets/static/dist-ftp-app/script2.js?v=${appVersion}"></script>
<script type="text/javascript" src="/assets/static/dist-ftp-app/script3.js?v=${appVersion}"></script>
</body>
</html>
