<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="minhasSessoes.list.title"/></title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div align="center">
    <h1><fmt:message key="minhasSessoes.list.title"/></h1>
    <p>
        <a href="${pageContext.request.contextPath}/usuario/"><fmt:message key="link.main_menu"/></a>
        &nbsp;|&nbsp;
        <a href="${pageContext.request.contextPath}/admin/projetos/lista"><fmt:message key="link.project.list"/></a>
    </p>
</div>

<c:if test="${mensagens != null && mensagens.isExisteErros()}">
    <div id="erro" class="erro-container" style="width: 80%; max-width:800px; margin: 15px auto;">
        <ul>
            <c:forEach var="erroKey" items="${mensagens.getErros()}">
                <li><fmt:message key="${erroKey}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

</body>
</html>