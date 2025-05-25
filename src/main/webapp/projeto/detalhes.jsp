<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<html>
<head>
    <title><fmt:message key="project.details.title" />: <c:out value="${requestScope.projeto.nome}"/></title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
    <style>
        .lang-switcher { text-align: right; margin-bottom: 10px; padding: 5px; width: 90%; max-width: 900px; margin-left: auto; margin-right: auto;}
        .lang-switcher a, .lang-switcher span { display: inline-block; padding: 5px 10px; margin-left: 5px; border: 1px solid #6d4c41; border-radius: 5px; text-decoration: none; color: #6d4c41; background-color: #fff8e1; font-size: 0.9em;}
        .lang-switcher a:hover { background-color: #d7ccc8; }
        .lang-switcher span { background-color: #6d4c41; color: white; font-weight: bold; }
        .actions-sessao { margin-top: 20px; padding: 15px; background-color: #fff8e1; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); width: 90%; max-width: 900px; margin-left:auto; margin-right:auto;}
        .details-container { width: 90%; max-width: 900px; margin: 20px auto; } /* Adicionado margin */
        table caption { font-size: 1.5em; font-weight: bold; padding: 15px; color: #3e2723; background-color: transparent; border-bottom: none; }
    </style>
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

<div class="lang-switcher">
    <c:choose>
        <c:when test="${sessionScope.lang == 'pt_BR' || sessionScope.lang == null}">
            <span>Português (BR)</span>
            <a href="?id=${requestScope.projeto.id}&lang=en_US">English (US)</a>
        </c:when>
        <c:otherwise>
            <a href="?id=${requestScope.projeto.id}&lang=pt_BR">Português (BR)</a>
            <span>English (US)</span>
        </c:otherwise>
    </c:choose>
</div>

<%-- A sua exibição de erros original é mantida (se houver no seu original) --%>
<c:if test="${!empty requestScope.mensagens}">
    <div id="erro" style="color: red; text-align:center; margin-bottom:15px;">
        <c:forEach items="${requestScope.mensagens}" var="mensagem">
            ${mensagem}<br/>
        </c:forEach>
    </div>
</c:if>

<c:if test="${requestScope.projeto == null}">
    <div align="center">
        <p><fmt:message key="project.details.naoEncontrado"/></p>
    </div>
</c:if>

<c:if test="${requestScope.projeto != null}">
    <div class="details-container">
        <table border="1">
            <caption><fmt:message key="project.details.caption" />: <c:out value="${requestScope.projeto.nome}"/></caption>
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
                <td class="descricao"><pre><c:out value="${requestScope.projeto.descricao}" /></pre></td>
            </tr>
            <tr>
                <th><fmt:message key="project.table.creation_date" /></th>
                <td><fmt:formatDate value="${requestScope.projeto.dataCriacao}" pattern="dd/MM/yyyy" /></td>
            </tr>
            <tr>
                <th><fmt:message key="project.table.members" /></th>
                <td>
                    <c:forEach var="membro" items="${requestScope.projeto.membros}">
                        <c:out value="${membro.nome}" /> (<c:out value="${membro.login}" />)<br/>
                    </c:forEach>
                </td>
            </tr>
        </table>
        <div class="actions-sessao" align="center">
            <h3><fmt:message key="sessao.listagem.tituloParaEsteProjeto"/></h3>
            <p>
                <a href="${pageContext.request.contextPath}/sessao/listaPorProjeto?idProjeto=${requestScope.projeto.id}">
                    <fmt:message key="projeto.link.verSessoes"/>
                </a>
                <c:if test="${sessionScope.usuarioLogado.papel == 'tester' || sessionScope.usuarioLogado.papel == 'admin'}">
                    &nbsp;|&nbsp;
                    <a href="${pageContext.request.contextPath}/sessao/cadastro?idProjeto=${requestScope.projeto.id}">
                        <fmt:message key="projeto.link.novaSessao"/>
                    </a>
                </c:if>
            </p>
        </div>
    </div>
</c:if>
<p style="text-align:center; margin-top:20px;">
    <a href="${pageContext.request.contextPath}/admin/projetos/lista"><fmt:message key="link.project.list"/></a>
</p>
</body>
</html>