<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title><fmt:message key="titulo.pagina.estrategias"/></title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/lista_layout.css">
</head>
<body>

<h1><fmt:message key="titulo.gerenciador.estrategias"/></h1>

<nav>
  <a href="${pageContext.request.contextPath}/usuario/"><fmt:message key="link.main_menu"/></a> |
  <a href="${pageContext.request.contextPath}/estrategia?acao=listar"><fmt:message key="link.listar_todas"/></a> |
  <a href="${pageContext.request.contextPath}/estrategia?acao=novo"><fmt:message key="link.nova_estrategia"/></a>
</nav>
<hr>

<h2><fmt:message key="titulo.lista.estrategias"/></h2>

<c:if test="${not empty param.sucesso}">
  <p class="mensagem sucesso">
    <c:choose>
      <c:when test="${param.sucesso == 'salvo'}"><fmt:message key="mensagem.sucesso.salvo"/></c:when>
      <c:when test="${param.sucesso == 'excluido'}"><fmt:message key="mensagem.sucesso.excluido"/></c:when>
    </c:choose>
  </p>
</c:if>

<c:if test="${not empty param.erro}">
  <p class="mensagem erro">
    <c:choose>
      <c:when test="${param.erro == 'nao_encontrado'}"><fmt:message key="mensagem.erro.nao_encontrado"/></c:when>
      <c:when test="${param.erro == 'true'}"><fmt:message key="mensagem.erro.generico"/></c:when>
      <c:otherwise>
        <fmt:message key="mensagem.erro.detalhado"/>: <c:out value="${param.erroMsg}" default="Ocorreu um erro."/>
      </c:otherwise>
    </c:choose>
  </p>
</c:if>

<c:if test="${not empty erroMsg}">
  <p class="mensagem erro"><c:out value="${erroMsg}"/></p>
</c:if>

<table>
  <thead>
  <tr>
    <th><fmt:message key="tabela.coluna.id"/></th>
    <th><fmt:message key="tabela.coluna.nome"/></th>
    <th><fmt:message key="tabela.coluna.descricao"/></th>
    <th><fmt:message key="tabela.coluna.acoes"/></th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="estrategia" items="${listaEstrategias}">
    <tr>
      <td>${estrategia.id}</td>
      <td><c:out value="${estrategia.nome}" /></td>
      <td><c:out value="${estrategia.descricao}" /></td>
      <td>
        <a href="${pageContext.request.contextPath}/estrategia?acao=editar&id=${estrategia.id}"><fmt:message key="acao.editar"/></a> |
        <a href="${pageContext.request.contextPath}/estrategia?acao=excluir&id=${estrategia.id}" onclick="return confirm('<fmt:message key="mensagem.confirmar_exclusao"/>');"><fmt:message key="acao.excluir"/></a> |
        <a href="${pageContext.request.contextPath}/estrategia?acao=detalhes&id=${estrategia.id}"><fmt:message key="acao.detalhes"/></a>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty listaEstrategias}">
    <tr>
      <td colspan="4"><fmt:message key="mensagem.nenhuma_estrategia"/></td>
    </tr>
  </c:if>
  </tbody>
</table>
</body>
</html>
