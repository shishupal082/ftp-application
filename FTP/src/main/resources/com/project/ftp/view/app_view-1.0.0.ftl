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
    <link rel="stylesheet" type="text/css" href="/assets/static/css/style.css?v=${appVersion}">
    <link rel="stylesheet" type="text/css" href="/assets/static/dist-auth-app/auth-style.css?v=${appVersion}">
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
           value="page=${pageName},app_version=${appVersion},is_guest_enable=${isGuestEnable},android_check_enable=${androidCheckEnable},displayCreatePasswordLinkEnable=${displayCreatePasswordLinkEnable}"/>
    <input type="hidden" style="display: none;" name="loginUserDetailsV2Str" id="loginUserDetailsV2Str" value="${loginUserDetailsV2Str}"/>
    <input type="hidden" style="display: none;" name="headingJson" id="headingJson"
            value="<#if ftlConfig.headingJson??>${ftlConfig.headingJson}<#else></#if>"/>
    <input type="hidden" style="display: none;" name="afterLoginLinkJson" id="afterLoginLinkJson"
           value="<#if ftlConfig.afterLoginLinkJson??>${ftlConfig.afterLoginLinkJson}<#else></#if>"/>
    <input type="hidden" style="display: none;" name="footerLinkJson" id="footerLinkJson"
           value="<#if ftlConfig.footerLinkJson??>${ftlConfig.footerLinkJson}<#else></#if>"/>
    <input type="hidden" style="display: none;" name="footerLinkJsonAfterLogin" id="footerLinkJsonAfterLogin"
           value="<#if ftlConfig.footerLinkJsonAfterLogin??>${ftlConfig.footerLinkJsonAfterLogin}<#else></#if>"/>
    <input type="hidden" style="display: none;" name="loginRedirectUrl" id="loginRedirectUrl"
           value="${loginRedirectUrl}"/>
    <input type="hidden" style="display: none;" name="createPasswordOtpInstruction" id="createPasswordOtpInstruction"
           value="<#if ftlConfig.createPasswordOtpInstruction??>${ftlConfig.createPasswordOtpInstruction}<#else></#if>"/>
</div>
<div id="root"></div>
<script type="text/javascript" src="/assets/static/libs/jquery-2.1.3.js"></script>
<script type="text/javascript">
var GLOBAL = {
    baseApi: "",
    basepathname: "",
    staticDataApi: "/api/get_static_data",
    relatedUsersDataApi: "/api/get_related_users_data",
    relatedUsersDataV2Api: "/api/get_related_users_data_v2",
    rolesConfigDataApi: "/api/get_roles_config"
};
GLOBAL.JQ = $;
GLOBAL.gaTrackingEnable = ${ftlConfig.gaTrackingEnable?c};
<#if ftlConfig.tempGaEnable??>
GLOBAL.gtag = gtag;
<#else>
GLOBAL.gtag = null;
</#if>
GLOBAL.pageData = document.getElementById("pageData").value;
GLOBAL.loginUserDetails = document.getElementById("loginUserDetailsV2Str").value;
GLOBAL.headingJson = document.getElementById("headingJson").value;
GLOBAL.afterLoginLinkJson = document.getElementById("afterLoginLinkJson").value;
GLOBAL.footerLinkJson = document.getElementById("footerLinkJson").value;
GLOBAL.footerLinkJsonAfterLogin = document.getElementById("footerLinkJsonAfterLogin").value;
GLOBAL.loginRedirectUrl = document.getElementById("loginRedirectUrl").value;
GLOBAL.createPasswordOtpInstruction = document.getElementById("createPasswordOtpInstruction").value;
window.GLOBAL = GLOBAL;
</script>

<script type="text/javascript" src="/assets/static/dist-react-base-1.0.0/script1.js?v=${appVersion}"></script>
<script type="text/javascript" src="/assets/static/dist-auth-app/script2.js?v=${appVersion}"></script>
<script type="text/javascript" src="/assets/static/dist-react-base-1.0.0/script3.js?v=${appVersion}"></script>
</body>
</html>
