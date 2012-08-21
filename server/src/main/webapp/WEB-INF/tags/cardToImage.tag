<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cards" required="true" type="java.util.List" %>

<c:forEach var="card" items="${cards}">
    <img src="/resources/img/cards/${card.nameForImage}_mini.png" alt="${card}" width="27" height="37">
</c:forEach>