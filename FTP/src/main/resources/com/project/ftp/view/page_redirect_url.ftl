<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page not found</title>
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
<body style="font-family: sans-serif;font-size: 24px;">
    <center id="display-dot">...</center>
    <center id="display-text" style="display:none">Invalid / Unauthorised redirect url</center>
    <div style="display:none"><input type="text" name="pageData" id="pageData" value="${pageData}"/></div>
<script type="text/javascript">
var pageData = document.getElementById("pageData").value;
function isValidUrl(reRouteUrl) {
    if (typeof reRouteUrl === "string") {
        reRouteUrl = reRouteUrl.trim();
        if (reRouteUrl === "/view/redirect") {
            return false;
        }
        if (reRouteUrl.length > 0) {
            return true;
        }
    }
    return false;
}
if (isValidUrl(pageData)) {
    window.location.href = pageData.trim();
} else {
    document.getElementById("display-dot").style.display="none";
    document.getElementById("display-text").style.display="block";
}
</script>
</body>
</html>