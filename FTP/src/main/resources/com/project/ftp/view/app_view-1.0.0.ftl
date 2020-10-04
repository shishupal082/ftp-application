<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <meta name="description" content="<#if ftlConfig.description??>${ftlConfig.description}<#else></#if>"/>
    <meta name="keywords" content="<#if ftlConfig.keywords??>${ftlConfig.keywords}<#else></#if>">
    <meta name="author" content="<#if ftlConfig.author??>${ftlConfig.author}<#else></#if>">
    <title><#if ftlConfig.title??>${ftlConfig.title}<#else></#if></title>
    <link rel="stylesheet" type="text/css" href="/assets/static/libs/bootstrap-v4.4.1.css">
    <link rel="stylesheet" type="text/css" href="/assets/static/dist-ftp-app/style.css?v=${appVersion}">
    <#if ftlConfig.tempGaEnable??>
        <!-- If condition -->
        <!-- Global site tag (gtag.js) - Google Analytics -->
        <script async src="https://www.googletagmanager.com/gtag/js?id=<#if ftlConfig.gaTrackingId??>${ftlConfig.gaTrackingId}<#else></#if>"></script>
        <script>
          window.dataLayer = window.dataLayer || [];
          function gtag(){dataLayer.push(arguments);}
          gtag('js', new Date());
          gtag('config', '<#if ftlConfig.gaTrackingId??>${ftlConfig.gaTrackingId}<#else></#if>');
        </script>
    <#else>
        <!-- Else condition -->
    </#if>
</head>
<body class="theme-grey">
<noscript>You need to enable JavaScript to run this app.</noscript>
<div style="display: none;">
    <input type="hidden" style="display: none;" name="pageData" id="pageData"
           value="page=${pageName},app_version=${appVersion},is_login=${isLogin},username=${userName},user_display_name=${userDisplayName},is_login_user_admin=${isLoginUserAdmin},upload_file_api_version=${uploadFileApiVersion},is_guest_enable=${isGuestEnable},is_forgot_password_enable=${isForgotPasswordEnable},display_create_password_link=${displayCreatePasswordLink}"/>
    <input type="hidden" style="display: none;" name="headingJson" id="headingJson"
            value="<#if ftlConfig.headingJson??>${ftlConfig.headingJson}<#else></#if>"/>
    <input type="hidden" style="display: none;" name="uploadFileInstruction" id="uploadFileInstruction"
            value="<#if ftlConfig.uploadFileInstruction??>${ftlConfig.uploadFileInstruction}<#else></#if>"/>
</div>
<div id="root"></div>
<script type="text/javascript" src="/assets/static/libs/jquery-2.1.3.js"></script>
<script type="text/javascript">
var GLOBAL = {
    baseapi: "",
    basepathname: "",
    JQ: $,
    isReactEnv: false
};
GLOBAL.gaTrackingEnable = ${ftlConfig.gaTrackingEnable?c};
<#if ftlConfig.tempGaEnable??>
GLOBAL.gtag = gtag;
<#else>
GLOBAL.gtag = null;
</#if>
GLOBAL.currentPageData = document.getElementById("pageData").value;
GLOBAL.headingJson = document.getElementById("headingJson").value;
GLOBAL.uploadFileInstruction = document.getElementById("uploadFileInstruction").value;
window.GLOBAL = GLOBAL;
</script>

<script type="text/javascript" src="/assets/static/dist-ftp-app/script1.js?v=${appVersion}"></script>
<script type="text/javascript" src="/assets/static/dist-ftp-app/script2.js?v=${appVersion}"></script>
<script type="text/javascript" src="/assets/static/dist-ftp-app/script3.js?v=${appVersion}"></script>
</body>
</html>
