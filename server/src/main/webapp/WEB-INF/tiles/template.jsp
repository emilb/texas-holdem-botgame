<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl">

<head>
    <title>Cygni Texas Hold'em</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <link href="<c:url value="/resources/css/bootstrap-2.2.2.min.css" />" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/resources/css/bootstrap-responsive-2.2.2.min.css" />" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/resources/css/prettify.css" />" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/resources/css/jquery.jqplot.min.css" />" rel="stylesheet" type="text/css"/>

    <link rel="icon" href="<c:url value="/resources/img/favicon.ico" />" type="image/png">
    <link rel="shortcut icon" href="<c:url value="/resources/img/favicon.ico" />" type="image/png">

    <script src="<c:url value="/resources/js/jquery-1.9.0.min.js" />"></script>
    <script src="<c:url value="/resources/js/bootstrap-2.2.2.min.js" />"></script>
    <script src="<c:url value="/resources/js/prettify/prettify.js" />"></script>
    <script src="<c:url value="/resources/js/underscore-1.4.3.min.js" />"></script>
    <script src="<c:url value="/resources/js/ICanHaz-0.10.min.js" />"></script>
</head>

<body>
<tiles:insertAttribute name="header" defaultValue=""/>
<!-- Page content -->
<tiles:insertAttribute name="body" defaultValue=""/>
<!-- End of page content -->
<tiles:insertAttribute name="footer" defaultValue=""/>
</body>
</html>