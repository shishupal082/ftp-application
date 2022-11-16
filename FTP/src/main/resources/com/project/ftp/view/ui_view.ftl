<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <#if uiViewObject.pageDescription??>
    <meta name="description" content="${uiViewObject.pageDescription}"/>
    </#if>
    <#if uiViewObject.pageKeywords??>
    <meta name="keywords" content="${uiViewObject.pageKeywords}">
    </#if>
    <#if uiViewObject.pageAuthor??>
    <meta name="author" content="${uiViewObject.pageAuthor}">
    </#if>
    <title><#if uiViewObject.pageTitle??>${uiViewObject.pageTitle}<#else></#if></title>
    <#if uiViewObject.cssFiles??>
    <#list uiViewObject.cssFiles as css_file>
    <link rel="stylesheet" type="text/css" href="${css_file}?v=${appVersion}">
    </#list>
    </#if>
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
<body <#if uiViewObject.bodyClass??>class="${uiViewObject.bodyClass}"</#if> >
    <noscript>You need to enable JavaScript to run this app.</noscript>
    <div id="root"><center>Loading...</center></div>
<#if uiViewObject.jQueryFilePath??>
<script type="text/javascript" src="${uiViewObject.jQueryFilePath}?v=${appVersion}"></script>
</#if>
<script type="text/javascript">
var GLOBAL = {
    baseApi: "<#if uiViewObject.baseApi??>${uiViewObject.baseApi}<#else></#if>",
    basepathname: "<#if uiViewObject.basePathName??>${uiViewObject.basePathName}<#else></#if>",
    appControlDataPath: "<#if uiViewObject.appControlDataPath??>${uiViewObject.appControlDataPath}<#else></#if>",
    appControlApi: "<#if uiViewObject.appControlApi??>${uiViewObject.appControlApi}<#else></#if>",
    projectHeading: "<#if uiViewObject.projectHeading??>${uiViewObject.projectHeading}<#else></#if>",
    forceLogin: ${forceLogin?c}
};
GLOBAL.appVersion = "${appVersion}";
<#if uiViewObject.jQueryFilePath??>
GLOBAL.JQ = $;
<#else>
GLOBAL.JQ = null;
</#if>
GLOBAL.gaTrackingEnable = ${ftlConfig.gaTrackingEnable?c};
<#if ftlConfig.tempGaEnable??>
GLOBAL.gtag = gtag;
<#else>
GLOBAL.gtag = null;
</#if>
<#if uiViewObject.validAppControl ??>
GLOBAL.validAppControl = [<#list uiViewObject.validAppControl as appControl>"${appControl}",</#list>];
</#if>
<#if uiViewObject.customPageData??>
GLOBAL.customPageData = {
<#list uiViewObject.customPageData?keys as key>
    "${key}": "${uiViewObject.customPageData[key]}",
</#list>
};
<#else>
GLOBAL.customPageData = {};
</#if>
window.GLOBAL = GLOBAL;
</script>
<#if uiViewObject.jsFiles??>
<#list uiViewObject.jsFiles as js_file>
<script type="text/javascript" src="${js_file}?v=${appVersion}"></script>
</#list>
</#if>
</body>
</html>
