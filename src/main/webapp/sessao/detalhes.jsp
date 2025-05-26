<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="sessao.detalhes.titulo" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<h1><fmt:message key="sessao.detalhes.titulo" /></h1>

<c:if test="${mensagens != null && mensagens.isExisteErros()}">
    <div id="erro">
        <ul>
            <c:forEach var="erroKey" items="${mensagens.getErros()}">
                <li><fmt:message key="${erroKey}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<c:if test="${sessao == null && (mensagens == null || !mensagens.isExisteErros()) && sessionScope.mensagensFlash == null}">
    <p><fmt:message key="sessao.detalhes.naoEncontrada" /></p>
</c:if>

<c:if test="${sessao != null}">
    <div class="session-details">
        <h2><fmt:message key="sessao.detalhes.infoSessao" /></h2>
        <table class="details-table">
            <tr><th><fmt:message key="sessao.detalhes.id" />:</th><td><c:out value="${sessao.id}" /></td></tr>
            <tr><th><fmt:message key="sessao.detalhes.projeto" />:</th><td><c:out value="${sessao.projeto.nome}" /></td></tr>
            <tr><th><fmt:message key="sessao.detalhes.tester" />:</th><td><c:out value="${sessao.tester.nome}" /></td></tr>
                <%--            <tr>--%>
                <%--                <th><fmt:message key="sessao.detalhes.idEstrategia" />:</th>--%>
                <%--                <td>--%>
                <%--                    <c:out value="${sessao.idEstrategia}" />--%>
                <%--                        &lt;%&ndash; Se você buscar o nome da estratégia no DAO, mesmo sem o objeto: &ndash;%&gt;--%>
                <%--                    <c:if test="${not empty sessao.estrategia.nome}"> (<c:out value="${sessao.estrategia.nome}" />)</c:if>--%>
                <%--                </td>--%>
                <%--            </tr>--%>
            <tr><th><fmt:message key="sessao.detalhes.duracao" />:</th><td><c:out value="${sessao.duracao}" /></td></tr>
            <tr><th><fmt:message key="sessao.detalhes.descricao" />:</th><td><pre><c:out value="${sessao.descricao}" /></pre></td></tr>
            <tr><th><fmt:message key="sessao.detalhes.statusAtual" />:</th><td><fmt:message key="status.${sessao.status.name()}"/></td></tr>
        </table>
    </div>

    <div class="status-actions">
        <h3><fmt:message key="sessao.detalhes.acoesStatus" /></h3>
        <c:if test="${sessao.status.name() == 'CREATED'}">
            <form method="post" action="${pageContext.request.contextPath}/sessoes/atualizaStatus" style="display: inline-block; margin-right: 10px;">
                <input type="hidden" name="idSessao" value="${sessao.id}" />
                <input type="hidden" name="novoStatus" value="in_execution" />
                <input type="submit" value="<fmt:message key="sessao.detalhes.botao.iniciar" />" />
            </form>
        </c:if>
        <c:if test="${sessao.status.name() == 'IN_EXECUTION'}">
            <form method="post" action="${pageContext.request.contextPath}/sessoes/atualizaStatus" style="display: inline-block; margin-right: 10px;">
                <input type="hidden" name="idSessao" value="${sessao.id}" />
                <input type="hidden" name="novoStatus" value="finalized" />
                <input type="submit" value="<fmt:message key="sessao.detalhes.botao.finalizar" />" />
            </form>
        </c:if>
        <c:if test="${sessao.status.name() == 'FINALIZED'}">
            <p><fmt:message key="sessao.detalhes.finalizadaSemAcoes" /></p>
        </c:if>

        <c:if test="${sessionScope.usuarioLogado.papel == 'admin'}">
            <form method="post" action="${pageContext.request.contextPath}/sessoes/remocao"
                  style="display: inline-block;"
                  onsubmit="return confirm('<fmt:message key="sessao.confirmarRemocao"/>');">
                <input type="hidden" name="idSessao" value="${sessao.id}" />
                <input type="submit" value="<fmt:message key="comum.botao.remover"/>" class="button-danger" /> <%-- Adicione .button-danger ao CSS se quiser um estilo diferente --%>
            </form>
        </c:if>
    </div>

    <div class="status-history">
        <h3><fmt:message key="sessao.detalhes.historicoStatus" /></h3>
        <c:if test="${not empty sessao.historicoStatus}">
            <table class="list-table">
                <thead>
                <tr>
                    <th><fmt:message key="sessao.historico.dataHora" /></th>
                    <th><fmt:message key="sessao.historico.statusAnterior" /></th>
                    <th><fmt:message key="sessao.historico.statusNovo" /></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="hist" items="${sessao.historicoStatus}">
                    <tr>
                        <td><fmt:formatDate value="${hist.dataHora}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty hist.statusAnterior}">
                                    <fmt:message key="status.const.${hist.statusAnterior}"/>
                                </c:when>
                                <c:otherwise>(<fmt:message key="sessao.historico.nenhum" />)</c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:message key="status.const.${hist.statusNovo}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty sessao.historicoStatus}">
            <p><fmt:message key="sessao.historico.vazio" /></p>
        </c:if>
    </div>
    <hr/>
    <p>
        <a href="${pageContext.request.contextPath}/sessoes/lista-projeto?idProjeto=${sessao.idProjeto}"><fmt:message key="comum.voltarListaSessoesProjeto" /></a> |
        <a href="${pageContext.request.contextPath}/sessoes/minhas-sessoes"><fmt:message key="comum.voltarMinhasSessoes" /></a> |
        <a href="${pageContext.request.contextPath}/admin/projetos"><fmt:message key="link.projects" /></a>
    </p>
</c:if>
</body>
</html>