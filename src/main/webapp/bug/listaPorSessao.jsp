<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<c:set var="sessaoObj" value="${requestScope.sessao}" />
<c:set var="listaBugs" value="${requestScope.listaBugs}" />

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="bug.listagem.pagina.titulo"/>: <fmt:message key="sessao.label.id"/> <c:out value="${sessaoObj.id}"/></title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
    <style>
        /* Estilos adicionais ou do seu lista_layout.css podem ser aplicados aqui */
        .action-links a { margin-right: 5px; margin-left: 5px;}
        .erro-container { border: 1px solid red; background-color: #ffecec; padding: 10px; margin-bottom: 15px; }
        .erro-container ul { margin: 0; padding-left: 20px; list-style-type: disc; }
        .info-contexto { background-color: #f0f0f0; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; }
        .info-contexto p { margin: 5px 0; }
    </style>
</head>
<body>
<div align="center">
    <h1>
        <fmt:message key="bug.listagem.cabecalho"/>: <fmt:message key="sessao.label.id"/> <c:out value="${sessaoObj.id}"/>
        <c:if test="${not empty sessaoObj.projeto.nome}">
            (<fmt:message key="projeto.label.nome"/>: <c:out value="${sessaoObj.projeto.nome}"/>)
        </c:if>
    </h1>
    <div class="info-contexto">
        <p><strong><fmt:message key="sessao.label.projeto"/>:</strong> <c:out value="${sessaoObj.projeto.nome}"/></p>
        <p><strong><fmt:message key="sessao.label.estrategia"/>:</strong> <c:out value="${sessaoObj.estrategia.nome}"/></p>
        <p><strong><fmt:message key="sessao.label.tester"/>:</strong> <c:out value="${sessaoObj.tester.nome}"/></p>
        <p><strong><fmt:message key="sessao.label.status"/>:</strong> <fmt:message key="status.${sessaoObj.status.name()}"/></p>
    </div>

    <p class="action-links">
        <c:if test="${sessionScope.usuarioLogado.id == sessaoObj.idTester || sessionScope.usuarioLogado.papel == 'admin'}">
            <a href="${pageContext.request.contextPath}/bugs/cadastro?idSessao=${sessaoObj.id}">
                <fmt:message key="bug.listagem.novoParaSessao"/>
            </a>
            &nbsp;|&nbsp;
        </c:if>
        <a href="${pageContext.request.contextPath}/sessoes/detalhes?idSessao=${sessaoObj.id}"><fmt:message key="comum.voltarDetalhesSessao"/></a>
        <c:if test="${not empty sessaoObj.projeto.id}">
            &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/sessoes/lista-projeto?idProjeto=${sessaoObj.projeto.id}"><fmt:message key="comum.voltarListaSessoesProjeto"/></a>
            &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/admin/projetos/detalhes?id=${sessaoObj.projeto.id}"><fmt:message key="comum.voltarDetalhesProjeto"/></a>
        </c:if>
        &nbsp;|&nbsp;
        <a href="${pageContext.request.contextPath}/usuario/"><fmt:message key="link.main_menu"/></a>
    </p>
</div>

<%-- Bloco de Erros --%>
<c:if test="${mensagens != null && mensagens.isExisteErros()}">
    <div class="erro-container" style="width: 80%; max-width:800px; margin: 15px auto;">
        <ul>
            <c:forEach var="erroKey" items="${mensagens.getErros()}">
                <li><fmt:message key="${erroKey}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>
<c:if test="${sessionScope.mensagensFlash != null && sessionScope.mensagensFlash.isExisteErros()}">
    <div class="erro-container" style="width: 80%; max-width:800px; margin: 15px auto;">
        <ul>
            <c:forEach var="erroKey" items="${sessionScope.mensagensFlash.getErros()}">
                <li><fmt:message key="${erroKey}"/></li>
            </c:forEach>
        </ul>
    </div>
    <c:remove var="mensagensFlash" scope="session" />
</c:if>


<c:if test="${empty listaBugs}">
    <p align="center"><fmt:message key="bug.listagem.nenhumEncontradoNaSessao"/></p>
</c:if>

<c:if test="${not empty listaBugs}">
    <div align="center" style="margin-top: 20px;">
        <table class="lista-tabela"> <%-- Use sua classe CSS de tabela aqui --%>
            <caption><fmt:message key="bug.listagem.caption"/></caption>
            <thead>
            <tr>
                <th><fmt:message key="bug.tabela.id"/></th>
                <th><fmt:message key="bug.tabela.descricao"/></th>
                <th><fmt:message key="bug.tabela.dataReporte"/></th>
                <th><fmt:message key="bug.tabela.resolvido"/></th>
                <th><fmt:message key="bug.tabela.acoes"/></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="bug" items="${listaBugs}">
                <tr>
                    <td><c:out value="${bug.id}"/></td>
                    <td><c:out value="${bug.descricao}"/></td>
                    <td>
                        <c:if test="${not empty bug.data}">
                            <fmt:formatDate value="${bug.dataAsTimestamp}" type="both" dateStyle="medium" timeStyle="short" />
                        </c:if>
                    </td>
                    <td style="text-align: center;">
                        <c:choose>
                            <c:when test="${bug.resolvido}">
                                <span style="color:green;"><fmt:message key="bug.resolvido"/></span>
                            </c:when>
                            <c:otherwise>
                                <span style="color:red;"><fmt:message key="bug.naoresolvido"/></span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${sessionScope.usuarioLogado.id == sessaoObj.idTester || sessionScope.usuarioLogado.papel == 'admin'}">
                            <a href="${pageContext.request.contextPath}/bugs/edicao?idBug=${bug.id}"><fmt:message key="button.edit"/></a>
                            <hr>
                            <a href="${pageContext.request.contextPath}/bugs/remocao?idBug=${bug.id}&idSessao=${sessaoObj.id}"
                               onclick="return confirm('<fmt:message key="bug.confirmarRemocao"/>');">
                                <fmt:message key="comum.botao.remover"/>
                            </a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>
</body>
</html>