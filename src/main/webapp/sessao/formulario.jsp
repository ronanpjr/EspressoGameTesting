<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />


<c:set var="isEditMode" value="${not empty requestScope.isEditMode ? requestScope.isEditMode : false}" />
<c:set var="sessaoObj" value="${requestScope.sessao}" />
<c:set var="projetoObj" value="${not empty sessaoObj.projeto ? sessaoObj.projeto : requestScope.projeto}" />


<!DOCTYPE html>
<html>
<head>
    <title>
        <c:choose>
            <c:when test="${isEditMode}">
                <fmt:message key="sessao.formulario.titulo.editar" />: <c:out value="${sessaoObj.id}"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="sessao.formulario.titulo.nova" />
            </c:otherwise>
        </c:choose>
    </title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <style>
        #erro { border: 1px solid red; background-color: #ffecec; padding: 10px; margin-bottom: 15px; }
        #erro ul { margin: 0; padding-left: 20px; list-style-type: disc; }
    </style>
</head>
<body>
<h1>
    <c:choose>
        <c:when test="${isEditMode}">
            <fmt:message key="sessao.formulario.titulo.editar" /> SESSÃO ID: <c:out value="${sessaoObj.id}"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="sessao.formulario.titulo.nova" />
        </c:otherwise>
    </c:choose>
</h1>

<%-- Bloco de Exibição de Erros (mantido como antes) --%>
<c:if test="${mensagens != null && mensagens.isExisteErros()}">
    <div id="erro">
        <ul>
            <c:forEach var="erroKey" items="${mensagens.getErros()}">
                <li><fmt:message key="${erroKey}"/>
                    <c:if test="${erroKey == 'sessao.erro.transicaoStatusInvalida' && not empty requestScope.statusAtual && not empty requestScope.statusNovo}">
                        (<fmt:message key="sessao.formulario.statusAtual"/>: <fmt:message key="status.${requestScope.statusAtual}"/>,
                        <fmt:message key="sessao.formulario.statusNovo"/>: <fmt:message key="status.${requestScope.statusNovo}"/>)
                    </c:if>
                </li>
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

<form method="post" action="${pageContext.request.contextPath}/sessoes/${isEditMode ? 'atualizacao' : 'insercao'}">

    <c:if test="${isEditMode}">
        <input type="hidden" name="idSessao" value="<c:out value='${sessaoObj.id}'/>" />
    </c:if>
    <input type="hidden" name="idProjeto" value="<c:out value='${not empty projetoObj.id ? projetoObj.id : sessaoObj.idProjeto}'/>" />

    <table>
        <tr>
            <th><fmt:message key="sessao.formulario.projeto" />:</th>
            <td><strong><c:out value="${projetoObj.nome}" /></strong></td>
        </tr>

        <%-- NOVO: Campo de Seleção de Estratégia --%>
        <tr>
            <th><label for="idEstrategia"><fmt:message key="sessao.formulario.estrategia" />:</label></th>
            <td>
                <%-- Se for modo de edição E a estratégia já estiver definida, mostra como texto e hidden --%>
                <%-- Se for modo de criação OU a estratégia não estiver definida, mostra dropdown --%>
                <c:choose>
                    <c:when test="${isEditMode and not empty sessaoObj.idEstrategia}">
                        <strong>
                            <c:out value="${sessaoObj.estrategia.nome}"/> (ID: <c:out value="${sessaoObj.idEstrategia}"/>)
                        </strong>
                        <input type="hidden" name="idEstrategia" value="<c:out value='${sessaoObj.idEstrategia}'/>" />
                        <br/><small><fmt:message key="sessao.formulario.estrategia.avisoEditar"/></small>
                    </c:when>
                    <c:otherwise>
                        <select name="idEstrategia" id="idEstrategia" required>
                            <option value=""><fmt:message key="comum.selecione" /></option>
                            <c:forEach var="estrategia" items="${requestScope.listaEstrategias}">
                                <option value="${estrategia.id}"
                                    ${ (not empty requestScope.idEstrategiaParam and requestScope.idEstrategiaParam == estrategia.id) or
                                            (empty requestScope.idEstrategiaParam and not empty sessaoObj.idEstrategia and sessaoObj.idEstrategia == estrategia.id)
                                            ? 'selected' : ''}
                                >
                                    <c:out value="${estrategia.nome}"/> (ID: <c:out value="${estrategia.id}"/>)
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${empty requestScope.listaEstrategias}">
                            <span style="color: red;"><fmt:message key="sessao.formulario.estrategia.nenhumaDisponivel"/></span>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>

        <tr>
            <th><label for="duracao"><fmt:message key="sessao.formulario.duracao" /> (HH:mm ou HH:mm:ss):</label></th>
            <td>
                <c:set var="duracaoValue">
                    <c:choose>
                        <c:when test="${not empty requestScope.duracaoParam}">${requestScope.duracaoParam}</c:when>
                        <c:when test="${not empty sessaoObj.duracao}">
                            <fmt:formatDate value="${sessaoObj.duracao}" pattern="HH:mm:ss" type="time" timeZone="UTC"/>
                        </c:when>
                        <c:otherwise></c:otherwise>
                    </c:choose>
                </c:set>
                <input type="time" name="duracao" id="duracao" step="1"
                       value="<c:out value='${duracaoValue}'/>" required />
            </td>
        </tr>
        <tr>
            <th><label for="descricao"><fmt:message key="sessao.formulario.descricao" />:</label></th>
            <td>
                <textarea name="descricao" id="descricao" rows="4" cols="30" required><c:out value='${not empty requestScope.descricaoParam ? requestScope.descricaoParam : sessaoObj.descricao}'/></textarea>
            </td>
        </tr>

        <c:if test="${isEditMode}">
            <tr>
                <th><label for="status"><fmt:message key="sessao.formulario.status" />:</label></th>
                <td>
                    <select name="status" id="status" required>
                        <c:forEach var="statusOpt" items="${requestScope.todosStatusSessao}">
                            <option value="${statusOpt.name()}"
                                ${ (not empty requestScope.statusParam and requestScope.statusParam == statusOpt.name()) or
                                        (empty requestScope.statusParam and not empty sessaoObj.status and sessaoObj.status.name() == statusOpt.name())
                                        ? 'selected' : ''}
                            >
                                <fmt:message key="status.${statusOpt.name()}"/>
                            </option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
        </c:if>

        <tr>
            <td colspan="2" style="text-align: center;">
                <input type="submit" value="<fmt:message key='sessao.formulario.botao.${isEditMode ? "atualizar" : "criar"}' />" />
            </td>
        </tr>
    </table>
</form>

<%-- Links de Navegação (mantido como antes) --%>
<p style="text-align: center; margin-top: 20px;">
    <c:choose>
        <c:when test="${isEditMode and not empty sessaoObj.id}">
            <a href="${pageContext.request.contextPath}/sessoes/detalhes?idSessao=<c:out value='${sessaoObj.id}'/>">
                <fmt:message key="comum.voltarDetalhesSessao" />
            </a>
            &nbsp;|&nbsp;
        </c:when>
    </c:choose>
    <a href="${pageContext.request.contextPath}/sessoes/listaPorProjeto?idProjeto=<c:out value='${not empty projetoObj.id ? projetoObj.id : sessaoObj.idProjeto}'/>">
        <fmt:message key="comum.voltarListaSessoesProjeto" />
    </a>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/admin/projetos/detalhes?id=<c:out value='${not empty projetoObj.id ? projetoObj.id : sessaoObj.idProjeto}'/>">
        <fmt:message key="comum.voltarDetalhesProjeto" />
    </a>
</p>
</body>
</html>
