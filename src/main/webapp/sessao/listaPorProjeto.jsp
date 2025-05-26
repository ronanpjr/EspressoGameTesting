<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html>
<head>
  <title><fmt:message key="sessoesPorProjeto.list.title"/>: <c:out value="${projeto.nome}"/></title>
  <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div align="center">
  <h1><fmt:message key="sessoesPorProjeto.list.title"/>: <c:out value="${projeto.nome}"/></h1>
  <p>
    <c:if test="${sessionScope.usuarioLogado.papel == 'tester' || sessionScope.usuarioLogado.papel == 'admin'}">
      <a href="${pageContext.request.contextPath}/sessoes/cadastro?idProjeto=${projeto.id}">
        <fmt:message key="sessao.listagem.nova"/>
      </a>
      &nbsp;|&nbsp;
    </c:if>
    <a href="${pageContext.request.contextPath}/admin/projetos/detalhes?id=${projeto.id}"><fmt:message key="comum.voltarDetalhesProjeto"/></a>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/admin/projetos/lista"><fmt:message key="link.project.list"/></a>
    &nbsp;|&nbsp;
    <a href="${pageContext.request.contextPath}/usuario/"><fmt:message key="link.main_menu"/></a>
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
<c:if test="${sessionScope.mensagensFlash != null && sessionScope.mensagensFlash.isExisteErros()}">
  <div id="erro" class="erro-container" style="width: 80%; max-width:800px; margin: 15px auto;">
    <ul>
      <c:forEach var="erroKey" items="${sessionScope.mensagensFlash.getErros()}">
        <li><fmt:message key="${erroKey}"/></li>
      </c:forEach>
    </ul>
  </div>
  <c:remove var="mensagensFlash" scope="session" />
</c:if>


<c:if test="${empty listaSessoes}">
  <p align="center"><fmt:message key="sessao.listagem.nenhumaEncontrada"/></p>
</c:if>

<c:if test="${not empty listaSessoes}">
  <div align="center">
    <table>
      <caption><fmt:message key="sessao.listagem.caption"/></caption>
      <thead>
      <tr>
        <th><fmt:message key="sessao.tabela.id"/></th>
        <th><fmt:message key="sessao.tabela.tester"/></th>
        <th><fmt:message key="sessao.tabela.estrategia"/></th>
        <th><fmt:message key="sessao.tabela.duracao"/></th>
        <th><fmt:message key="sessao.tabela.descricao"/></th>
        <th><fmt:message key="sessao.tabela.status"/></th>
        <th><fmt:message key="sessao.tabela.acoes"/></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="sessao" items="${listaSessoes}">
        <tr>
          <td><c:out value="${sessao.id}"/></td>
          <td><c:out value="${sessao.tester.nome}"/></td>
                      <td>
                        <c:out value="${sessao.idEstrategia}"/>
                        <c:if test="${not empty sessao.estrategia.nome}"> (<c:out value="${sessao.estrategia.nome}"/>)</c:if>
                      </td>
          <td><c:out value="${sessao.duracao}"/></td>
          <td><c:out value="${sessao.descricao}"/></td>
          <td><fmt:message key="status.${sessao.status.name()}"/></td>
          <td>
            <c:if test="${sessionScope.usuarioLogado.id == sessao.idTester || sessionScope.usuarioLogado.papel == 'admin'}">
              <a href="${pageContext.request.contextPath}/sessoes/detalhes?idSessao=${sessao.id}"><fmt:message key="comum.link.detalhes"/></a>
              <hr>
              <a href="${pageContext.request.contextPath}/sessoes/edicao?idSessao=${sessao.id}"><fmt:message key="button.edit"/></a>
              <hr>
              <a href="${pageContext.request.contextPath}/sessoes/remocao?idSessao=${sessao.id}"
                 onclick="return confirm('<fmt:message key="sessao.confirmarRemocao"/>');">
                <fmt:message key="comum.botao.remover"/>
              </a>
              <hr>
              <a href="${pageContext.request.contextPath}/bugs/lista-sessao?idSessao=${sessao.id}">
                <fmt:message key="sessao.tabela.verBugs"/>
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