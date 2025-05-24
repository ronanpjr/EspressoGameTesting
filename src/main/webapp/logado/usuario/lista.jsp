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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- ADICIONADO: Taglib fmt --%>

<%-- ADICIONADO: Define o locale e o bundle --%>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<html>
<head>
    <title><fmt:message key="app.title" /></title> <%-- MODIFICADO --%>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
    <style>
        /* Estilos básicos para os botões/links de idioma (mesmos do login.jsp) */
        .lang-switcher { text-align: right; margin-bottom: 10px; padding: 5px; }
        .lang-switcher a, .lang-switcher span { display: inline-block; padding: 5px 10px; margin-left: 5px; border: 1px solid #6d4c41; border-radius: 5px; text-decoration: none; color: #6d4c41; background-color: #fff8e1; font-size: 0.9em; }
        .lang-switcher a:hover { background-color: #d7ccc8; }
        .lang-switcher span { background-color: #6d4c41; color: white; font-weight: bold; }
    </style>
</head>
<body>



<div align="center">
    <h1><fmt:message key="user.management.title" /></h1> <%-- MODIFICADO --%>
    <h2>
        <%-- CORRIGIDO: Uso de pageContext.request.contextPath --%>
        <a href="${pageContext.request.contextPath}/"><fmt:message key="link.main_menu" /></a> <%-- MODIFICADO --%>
        &nbsp;&nbsp;&nbsp;
        <a href="${pageContext.request.contextPath}/usuario/cadastro"><fmt:message key="link.user.add" /></a> <%-- MODIFICADO --%>
    </h2>
</div>

<div align="center">
    <table border="1">
        <caption><fmt:message key="user.list.title" /></caption> <%-- MODIFICADO --%>
        <tr>
            <th><fmt:message key="user.table.id" /></th> <%-- MODIFICADO --%>
            <th><fmt:message key="user.table.name" /></th> <%-- MODIFICADO --%>
            <th><fmt:message key="user.table.login" /></th> <%-- MODIFICADO --%>
            <th><fmt:message key="user.table.role" /></th> <%-- MODIFICADO --%>
            <th><fmt:message key="user.table.actions" /></th> <%-- MODIFICADO --%>
        </tr>
        <%-- ADICIONADO: Mensagem de confirmação para JS --%>
        <fmt:message key="confirm.user.delete" var="confirmMsg" />

        <c:forEach var="usuario" items="${requestScope.listaUsuarios}">
            <tr>
                <td>${usuario.id}</td>
                <td><c:out value="${usuario.nome}"/></td> <%-- Use c:out para segurança --%>
                <td><c:out value="${usuario.login}"/></td> <%-- Use c:out para segurança --%>
                <td>
                        <%-- Traduz o papel --%>
                    <fmt:message key="role.${usuario.papel}" />
                </td>
                <td>
                        <%-- CORRIGIDO: Uso de pageContext.request.contextPath --%>
                    <a href="${pageContext.request.contextPath}/usuario/edicao?id=${usuario.id}"><fmt:message key="button.edit" /></a> <%-- MODIFICADO --%>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <a href="${pageContext.request.contextPath}/usuario/remocao?id=${usuario.id}"
                       onclick="return confirm('${confirmMsg}');"> <%-- MODIFICADO: Usa variável --%>
                        <fmt:message key="button.remove" /> <%-- MODIFICADO --%>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>