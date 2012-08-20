<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="section" required="true" %>

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="#">
                Cygni Texas Hold'em
            </a>
            <ul class="nav">
                <c:if test="${section == 'home'}">
                    <c:set var="currentClass">class="active"</c:set>
                </c:if>
                <li ${currentClass}>
                    <a href="/">Home</a>
                </li>

                <c:set var="currentClass"></c:set>
                <c:if test="${section == 'serverstatus'}">
                    <c:set var="currentClass">class="active"</c:set>
                </c:if>
                <li ${currentClass}><a href="/serverstatus">Server Status</a></li>


                <c:set var="currentClass"></c:set>
                <c:if test="${section == 'tournament'}">
                    <c:set var="currentClass">class="active"</c:set>
                </c:if>
                <li ${currentClass}><a href="/tournament">Tournament</a></li>

                <c:set var="currentClass"></c:set>
                <c:if test="${section == 'rules'}">
                    <c:set var="currentClass">class="active"</c:set>
                </c:if>
                <li ${currentClass}><a href="/rules">House rules</a></li>
            </ul>
        </div>
    </div>
</div>
