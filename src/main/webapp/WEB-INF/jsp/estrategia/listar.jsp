<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Estratégias - CRUD Simplificado</title>
  <style>
    body { font-family: sans-serif; margin: 20px; }
    table { border-collapse: collapse; width: 100%; margin-bottom: 20px;}
    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
    th { background-color: #f0f0f0; }
    .mensagem { padding: 10px; margin-bottom: 15px; border: 1px solid; }
    .sucesso { background-color: #e6ffe6; border-color: #99ff99; color: #006600; }
    .erro { background-color: #ffe6e6; border-color: #ff9999; color: #cc0000; }
    nav a { margin-right: 10px; }
  </style>
</head>
<body>
<h1>Gerenciador de Estratégias (Simplificado)</h1>
<nav>
  <a href="${pageContext.request.contextPath}/estrategia?acao=listar">Listar Todas</a> |
  <a href="${pageContext.request.contextPath}/estrategia?acao=novo">Nova Estratégia</a>
</nav>
<hr>

<h2>Lista de Estratégias</h2>
<p>teste115</p>

<c:if test="${not empty param.sucesso}">
  <p class="mensagem sucesso">
    <c:choose>
      <c:when test="${param.sucesso == 'salvo'}">Estratégia salva com sucesso!</c:when>
      <c:when test="${param.sucesso == 'excluido'}">Estratégia excluída com sucesso!</c:when>
    </c:choose>
  </p>
</c:if>
<c:if test="${not empty param.erro}">
  <p class="mensagem erro">
    <c:choose>
      <c:when test="${param.erro == 'nao_encontrado'}">Estratégia não encontrada.</c:when>
      <c:when test="${param.erro == 'true'}">Ocorreu um erro.</c:when>
      <c:otherwise>Erro: <c:out value="${param.erroMsg}" default="Ocorreu um erro."/></c:otherwise>
    </c:choose>
  </p>
</c:if>
<c:if test="${not empty erroMsg}">
  <p class="mensagem erro"><c:out value="${erroMsg}"/></p>
</c:if>

<table>
  <thead>
  <tr>
    <th>ID</th>
    <th>Nome</th>
    <th>Descrição</th>
    <th>Ações</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="estrategia" items="${listaEstrategias}">
    <tr>
      <td>${estrategia.id}</td>
      <td><c:out value="${estrategia.nome}" /></td>
      <td><c:out value="${estrategia.descricao}" /></td>
      <td>
        <a href="${pageContext.request.contextPath}/estrategia?acao=editar&id=${estrategia.id}">Editar</a> |
        <a href="${pageContext.request.contextPath}/estrategia?acao=excluir&id=${estrategia.id}"
           onclick="return confirm('Tem certeza?');">Excluir</a> |
        <a href="${pageContext.request.contextPath}/estrategia?acao=detalhes&id=${estrategia.id}">Detalhes</a>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty listaEstrategias}">
    <tr>
      <td colspan="4">Nenhuma estratégia cadastrada.</td>
    </tr>
  </c:if>
  </tbody>
</table>
</body>
</html>
