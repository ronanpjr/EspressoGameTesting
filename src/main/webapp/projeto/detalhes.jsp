<<<<<<< HEAD
<%--
  Created by IntelliJ IDEA.
  User: ronan
  Date: 25/05/2025
  Time: 14:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
</head>
<body>
$END$
</body>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<html>
<head>
    <title><fmt:message key="project.details.title" /></title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<div align="center">
    <h1><fmt:message key="project.details.title" /></h1>
    <h2>
        <a href="${pageContext.request.contextPath}/logado/usuario/main.jsp">
            <fmt:message key="link.main_menu" />
        </a>
        &nbsp;&nbsp;&nbsp;
        <a href="${pageContext.request.contextPath}/admin/projetos/lista">
            <fmt:message key="link.project.list" />
        </a>
    </h2>
</div>

<div align="center">
    <table border="1">
        <caption><fmt:message key="project.details.caption" /></caption>
        <tr>
            <th><fmt:message key="project.table.id" /></th>
            <td>${requestScope.projeto.id}</td>
        </tr>
        <tr>
            <th><fmt:message key="project.table.name" /></th>
            <td><c:out value="${requestScope.projeto.nome}" /></td>
        </tr>
        <tr>
            <th><fmt:message key="project.table.description" /></th>
            <td class="descricao"><c:out value="${requestScope.projeto.descricao}" /></td>
        </tr>
        <tr>
            <th><fmt:message key="project.table.creation_date" /></th>
            <td><fmt:formatDate value="${requestScope.projeto.dataCriacao}" pattern="dd/MM/yyyy" /></td>
        </tr>
        <tr>
            <th><fmt:message key="project.table.members" /></th>
            <td>
                <c:forEach var="membro" items="${requestScope.projeto.membros}">
                    <c:out value="${membro.nome}" /><br/>
                </c:forEach>
            </td>
        </tr>
    </table>
</div>

</body>
</html>
