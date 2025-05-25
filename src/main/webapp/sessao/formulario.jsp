<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="sessao.formulario.titulo.nova" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<h1><fmt:message key="sessao.formulario.titulo.nova" /></h1>

<c:if test="${mensagens != null && mensagens.isExisteErros()}">
    <div id="erro">
        <ul>
            <c:forEach var="erroKey" items="${mensagens.getErros()}">
                <li><fmt:message key="${erroKey}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/sessao/insercao">
    <input type="hidden" name="idProjeto" value="<c:out value='${projeto.id}' default='${param.idProjeto}'/>" />

    <table>
        <tr>
            <th><fmt:message key="sessao.formulario.projeto" />:</th>
            <td><strong><c:out value="${projeto.nome}" /></strong></td>
        </tr>
        <tr>
<%--            <th><label for="idEstrategia"><fmt:message key="sessao.formulario.estrategia" />:</label></th>--%>
<%--            <td>--%>
<%--                <c:choose>--%>
<%--                    <c:when test="${not empty estrategias}">--%>
<%--                        <select name="idEstrategia" id="idEstrategia" required>--%>
<%--                            <option value=""><fmt:message key="sessao.formulario.selecioneEstrategia" /></option>--%>
<%--                            <c:forEach var="estrategiaIt" items="${estrategias}"> &lt;%&ndash; Renomeada variável para evitar conflito com 'sessao.estrategia' &ndash;%&gt;--%>
<%--                                <option value="${estrategiaIt.id}" ${param.idEstrategia == estrategiaIt.id ? 'selected' : ''}>--%>
<%--                                    <c:out value="${estrategiaIt.nome}" />--%>
<%--                                </option>--%>
<%--                            </c:forEach>--%>
<%--                        </select>--%>
<%--                    </c:when>--%>
<%--                    <c:otherwise>--%>
<%--                        &lt;%&ndash; Fallback se a lista de estratégias não for carregada &ndash;%&gt;--%>
<%--                        <input type="number" name="idEstrategia" id="idEstrategia"--%>
<%--                               value="<c:out value='${not empty param.idEstrategia ? param.idEstrategia : requestScope.idEstrategia}'/>" required placeholder="ID da Estratégia"/>--%>
<%--                        <br/><em><small><fmt:message key="sessao.formulario.avisoEstrategiaIdOuCadastre" /></small></em>--%>
<%--                    </c:otherwise>--%>
<%--                </c:choose>--%>
<%--            </td>--%>
        </tr>
        <tr>
            <th><label for="duracao"><fmt:message key="sessao.formulario.duracao" /> (HH:mm):</label></th>
            <td>
                <input type="time" name="duracao" id="duracao"
                       value="<c:out value='${not empty param.duracao ? param.duracao : requestScope.duracao}'/>" required />
            </td>
        </tr>
        <tr>
            <th><label for="descricao"><fmt:message key="sessao.formulario.descricao" />:</label></th>
            <td>
                <textarea name="descricao" id="descricao" rows="4" cols="50" required><c:out value='${not empty param.descricao ? param.descricao : requestScope.descricao}'/></textarea>
            </td>
        </tr>
        <tr>
            <td colspan="2" style="text-align: center;">
                <input type="submit" value="<fmt:message key="sessao.formulario.botao.criar" />" />
            </td>
        </tr>
    </table>
</form>

<p style="text-align: center; margin-top: 20px;">
    <a href="${pageContext.request.contextPath}/sessao/listaPorProjeto?idProjeto=<c:out value='${projeto.id}' default='${param.idProjeto}'/>">
        <fmt:message key="comum.voltarListaSessoesProjeto" />
    </a>
    |
    <a href="${pageContext.request.contextPath}/admin/projetos/detalhes?id=<c:out value='${projeto.id}' default='${param.idProjeto}'/>">
        <fmt:message key="comum.voltarDetalhesProjeto" />
    </a>
</p>
</body>
</html>