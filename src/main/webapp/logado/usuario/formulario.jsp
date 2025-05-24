<%--
  Created by IntelliJ IDEA.
  User: ronan
  Date: 09/05/2025
  Time: 19:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="i18n.messages" />
<html>
<head>
    <title>Livraria Virtual</title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>

</head>

<body>
<div align="center">
    <h1>Gerenciamento de Usuários</h1>
    <h2>
        <a href="lista">Lista de Usuários</a>
    </h2>
</div>
<div align="center">
    <c:choose>
        <c:when test="${usuario != null}">
            <form action="atualizacao" method="post">
                <%@include file="campos.jsp"%>
            </form>
        </c:when>
        <c:otherwise>
            <form action="insercao" method="post">
                <%@include file="campos.jsp"%>
            </form>
        </c:otherwise>
    </c:choose>
</div>
<c:if test="${!empty requestScope.mensagens}">
    <ul class="erro">
        <c:forEach items="${requestScope.mensagens}" var="mensagem">
            <li>${mensagem}</li>
        </c:forEach>
    </ul>
</c:if>
</body>
</html>