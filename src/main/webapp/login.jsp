<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Define o locale baseado no atributo da sessão (ou usa o padrão pt_BR se não definido) --%>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<%-- Define o bundle base (pacote de recursos) --%>
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="login.title" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <%-- Bloco <style> removido --%>
</head>
<body>

<h1><fmt:message key="welcome.title1" /></h1>
<h2><fmt:message key="welcome.subtitle" /></h2>

<%-- Botões/Links para trocar idioma --%>
<div class="lang-switcher">
    <c:choose>
        <c:when test="${sessionScope.lang == 'pt_BR' || sessionScope.lang == null}">
            <span>Português (BR)</span>
            <a href="${pageContext.request.contextPath}/index.jsp?lang=en_US">English (US)</a>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/index.jsp?lang=pt_BR">Português (BR)</a>
            <span>English (US)</span>
        </c:otherwise>
    </c:choose>
</div>

<h1><fmt:message key="login.title" /></h1>

<c:if test="${mensagens.existeErros}">
    <div id="erro">
        <ul>
            <c:forEach var="erroKey" items="${mensagens.erros}">
                <li> <fmt:message key="${erroKey}" /> </li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/index.jsp">
    <table>
        <tr>
            <th><label for="login"><fmt:message key="login.username" /></label></th>
            <td><input type="text" name="login" id="login"
                       value="<c:out value='${param.login}'/>"/></td>
        </tr>
        <tr>
            <th><label for="senha"><fmt:message key="login.password" /></label></th>
            <td><input type="password" name="senha" id="senha" /></td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" name="bOK" value="<fmt:message key="login.button" />"/>
            </td>
        </tr>
    </table>
</form>
</body>
</html>