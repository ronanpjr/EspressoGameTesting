<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<%-- Variáveis de controle do formulário --%>
<c:set var="isEditModeBug" value="${not empty requestScope.isEditModeBug ? requestScope.isEditModeBug : false}" />
<c:set var="bugObj" value="${requestScope.bug}" /> <%-- Objeto Bug para edição ou repopulação em erro --%>
<c:set var="sessaoObj" value="${requestScope.sessao}" /> <%-- Objeto SessaoTeste pai, fornecido pelo controller --%>
<c:set var="idSessaoContexto" value="${not empty sessaoObj.id ? sessaoObj.id : requestScope.idSessao}" />


<!DOCTYPE html>
<html>
<head>
    <title>
        <c:choose>
            <c:when test="${isEditModeBug}">
                <fmt:message key="bug.formulario.titulo.editar" />: <c:out value="${bugObj.id}"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="bug.formulario.titulo.novo" />
            </c:otherwise>
        </c:choose>
    </title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <style>
        #erro { border: 1px solid red; background-color: #ffecec; padding: 10px; margin-bottom: 15px; }
        #erro ul { margin: 0; padding-left: 20px; list-style-type: disc; }
        .info-sessao { background-color: #f0f0f0; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; }
        .info-sessao strong { display: inline-block; min-width: 150px; }
    </style>
</head>
<body>
<h1>
    <c:choose>
        <c:when test="${isEditModeBug}">
            <fmt:message key="bug.formulario.titulo.editar" /> - BUG ID: <c:out value="${bugObj.id}"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="bug.formulario.titulo.novo" />
        </c:otherwise>
    </c:choose>
</h1>

<%-- Bloco de Exibição de Erros --%>
<c:if test="${mensagens != null && mensagens.isExisteErros()}">
    <div id="erro">
        <ul>
            <c:forEach var="erroKey" items="${mensagens.getErros()}">
                <li><fmt:message key="${erroKey}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>
<c:if test="${sessionScope.mensagensFlash != null && sessionScope.mensagensFlash.isExisteErros()}">
    <div id="erro">
        <ul>
            <c:forEach var="erroKey" items="${sessionScope.mensagensFlash.getErros()}">
                <li><fmt:message key="${erroKey}"/></li>
            </c:forEach>
        </ul>
    </div>
    <c:remove var="mensagensFlash" scope="session" />
</c:if>

<%-- Informações da Sessão de Teste Pai --%>
<c:if test="${not empty sessaoObj}">
    <div class="info-sessao">
        <h3><fmt:message key="bug.formulario.contextoSessao" /></h3>
        <p><strong><fmt:message key="sessao.label.projeto" />:</strong> <c:out value="${sessaoObj.projeto.nome}" /></p>
        <p><strong><fmt:message key="sessao.label.id" />:</strong> <c:out value="${sessaoObj.id}" /></p>
        <p><strong><fmt:message key="sessao.label.estrategia" />:</strong> <c:out value="${sessaoObj.estrategia.nome}" /></p>
        <p><strong><fmt:message key="sessao.label.tester" />:</strong> <c:out value="${sessaoObj.tester.nome}" /></p>
    </div>
</c:if>


<form method="post" action="${pageContext.request.contextPath}/bugs/${isEditModeBug ? 'atualizacao' : 'insercao'}">

    <%-- Campos Ocultos --%>
    <c:if test="${isEditModeBug}">
        <input type="hidden" name="idBug" value="<c:out value='${bugObj.id}'/>" />
    </c:if>
    <%-- idSessao é crucial para criar novo bug ou para contexto na atualização se necessário --%>
    <input type="hidden" name="idSessao" value="<c:out value='${idSessaoContexto}'/>" />

    <table>
        <tr>
            <th><label for="descricao"><fmt:message key="bug.formulario.descricao" />:</label></th>
            <td>
                <textarea name="descricao" id="descricao" rows="5" cols="50" required><c:out value='${bugObj.descricao}'/></textarea>
            </td>
        </tr>
        <tr>
            <th><label for="resolvido"><fmt:message key="bug.formulario.resolvido" />:</label></th>
            <td>
                <input type="checkbox" name="resolvido" id="resolvido" value="true" ${bugObj.resolvido ? 'checked' : ''} />
            </td>
        </tr>
        <c:if test="${isEditModeBug and not empty bugObj.data}">
            <tr>
                <th><fmt:message key="bug.formulario.dataReporte" />:</th>
                <td>
                    <fmt:formatDate value="${bugObj.dataAsTimestamp}" type="both" dateStyle="medium" timeStyle="medium" />
                </td>
            </tr>
        </c:if>

        <tr>
            <td colspan="2" style="text-align: center;">
                <input type="submit" value="<fmt:message key='bug.formulario.botao.${isEditModeBug ? "atualizar" : "criar"}' />" />
            </td>
        </tr>
    </table>
</form>

<p style="text-align: center; margin-top: 20px;">
    <a href="${pageContext.request.contextPath}/sessoes/detalhes?idSessao=<c:out value='${idSessaoContexto}'/>">
        <fmt:message key="comum.voltarDetalhesSessao" />
    </a>
</p>
</body>
</html>