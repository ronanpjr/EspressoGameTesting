<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<html>
<head>
    <title><fmt:message key="app.title" /></title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
    <style>
        .lang-switcher { text-align: right; margin-bottom: 10px; padding: 5px; }
        .lang-switcher a, .lang-switcher span { display: inline-block; padding: 5px 10px; margin-left: 5px; border: 1px solid #6d4c41; border-radius: 5px; text-decoration: none; color: #6d4c41; background-color: #fff8e1; font-size: 0.9em; }
        .lang-switcher a:hover { background-color: #d7ccc8; }
        .lang-switcher span { background-color: #6d4c41; color: white; font-weight: bold; }
    </style>
</head>
<body>

<div align="center">
    <h1><fmt:message key="user.management.title" /></h1>
    <h2>
        <a href="${pageContext.request.contextPath}/logado/usuario/main.jsp"><fmt:message key="link.main_menu" /></a>
        &nbsp;&nbsp;&nbsp;

        <c:if test="${sessionScope.usuarioLogado.papel == 'admin'}">
            <a href="${pageContext.request.contextPath}/usuario/cadastro">
                <fmt:message key="link.user.add" />
            </a>
        </c:if>
    </h2>
</div>

<div align="center">
    <table border="1">
        <caption><fmt:message key="user.list.title" /></caption>
        <tr>
            <th><fmt:message key="user.table.id" /></th>
            <th><fmt:message key="user.table.name" /></th>
            <th><fmt:message key="user.table.login" /></th>
            <th><fmt:message key="user.table.role" /></th>
            <th><fmt:message key="user.table.actions" /></th>
        </tr>
        <fmt:message key="confirm.user.delete" var="confirmMsg" />

        <c:forEach var="usuario" items="${requestScope.listaUsuarios}">
            <tr>
                <td>${usuario.id}</td>
                <td><c:out value="${usuario.nome}"/></td>
                <td><c:out value="${usuario.login}"/></td>
                <td><fmt:message key="role.${usuario.papel}" /></td>
                <td>
                    <!-- EXIBE BOTÕES DE AÇÃO SOMENTE SE FOR ADMIN -->
                    <c:if test="${sessionScope.usuarioLogado.papel == 'admin'}">
                        <a href="${pageContext.request.contextPath}/usuario/edicao?id=${usuario.id}">
                            <fmt:message key="button.edit" />
                        </a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="${pageContext.request.contextPath}/usuario/remocao?id=${usuario.id}"
                           onclick="return confirm('${confirmMsg}');">
                            <fmt:message key="button.remove" />
                        </a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
