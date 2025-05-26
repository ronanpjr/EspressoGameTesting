<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<html>
<head>
    <meta charset="UTF-8" />
    <title><fmt:message key="app.title" /></title>
    <link href="${pageContext.request.contextPath}/main.css" rel="stylesheet" />

</head>
<body>
<div class="nav-container">
    <h1> <fmt:message key="welcome.title2"/> <c:out value="${sessionScope.usuarioLogado.nome}"/>! </h1>
    <h2><fmt:message key="welcome.subtitle" /></h2>
    <ul>
        <li>
            <a href="${pageContext.request.contextPath}/usuario/lista">
                <fmt:message key="link.user.list" />
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/admin/projetos/">
                <fmt:message key="link.projects"/>
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/sessoes/minhas-sessoes">
                <fmt:message key="link.mysessions"/>
            </a>
        </li>
        <li><a href="${pageContext.request.contextPath}/estrategia">Estrategias</a></li>
        <li>
            <a href="${pageContext.request.contextPath}/logout.jsp">
                <fmt:message key="link.login"/>
            </a>
        </li>

        <!-- Adicione aqui outros módulos do sistema -->
    </ul>
</div>

<div class="content">
    <!-- Conteúdo introdutório ou dashboard resumo pode ser adicionado aqui -->
</div>
</body>
</html>
