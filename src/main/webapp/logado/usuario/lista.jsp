<%--
  Created by IntelliJ IDEA.
  User: ronan
  Date: 09/05/2025
  Time: 19:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Livraria Virtual</title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div align="center">
    <h1>Gerenciamento de Usuários</h1>
    <h2>
        <a href="/${requestScope.contextPath}">Menu Principal</a> &nbsp;&nbsp;&nbsp; <a
            href="/${requestScope.contextPath}/usuario/cadastro">Adicione Novo Usuário</a>
    </h2>
</div>

<div align="center">
    <table border="1">
        <caption>Lista de Usuários</caption>
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Login</th>
            <th>Papel</th>
            <th>Ações</th>
        </tr>
        <c:forEach var="usuario" items="${requestScope.listaUsuarios}">
            <tr>
                <td>${usuario.id}</td>
                <td>${usuario.nome}</td>
                <td>${usuario.login}</td>
                <td>${usuario.papel}</td>
                <td><a href="/${requestScope.contextPath}/usuario/edicao?id=${usuario.id}">Edição</a>
                    &nbsp;&nbsp;&nbsp;&nbsp; <a
                            href="/${requestScope.contextPath}/usuario/remocao?id=${usuario.id}"
                            onclick="return confirm('Tem certeza de que deseja excluir este usuário?');">
                        Remoção </a></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
