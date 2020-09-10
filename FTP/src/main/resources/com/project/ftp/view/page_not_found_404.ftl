<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page not found</title>
    <#if ftlConfig.gaTrackingId??>
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
<body>
    <center>Page not found : 404</center>
    <center><a href="/">Goto Home page</a></center>
</body>
</html>