<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Sistema de Projetos</title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .form-container {
            width: 60%;
            margin: 0 auto;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="form-container">
    <h1>Gerenciamento de Projetos</h1>
    <h2><a href="lista">Lista de Projetos</a></h2>

    <c:choose>
        <c:when test="${projeto != null && projeto.id != null}">
            <form action="atualizacao" method="post">
                <input type="hidden" name="id" value="${projeto.id}" />
        </c:when>
        <c:otherwise>
            <form action="insercao" method="post">
        </c:otherwise>
    </c:choose>

        <jsp:include page="campos.jsp" />

    </form>

    <c:if test="${!empty requestScope.mensagens}">
        <ul style="color: red;">
            <c:forEach items="${requestScope.mensagens}" var="mensagem">
                <li>${mensagem}</li>
            </c:forEach>
        </ul>
    </c:if>
</div>

</body>
</html>
