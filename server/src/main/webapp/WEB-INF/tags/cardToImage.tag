<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cards" required="true" type="java.util.List" %>

<c:forEach var="card" items="${cards}">
    <img src="/resources/img/cards/${card.nameForImage}_medium.png" alt="${card}" width="36" height="50" rel="tooltip"
         data-placement="top" data-original-title="${card}"/>
</c:forEach>