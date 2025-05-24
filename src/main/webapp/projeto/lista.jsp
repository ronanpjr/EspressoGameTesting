<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Sistema de Projetos</title>
</head>
<body>
<div align="center">
    <h1>Gerenciamento de Projetos</h1>
    <h2>
        <a href="${pageContext.request.contextPath}/admin">Menu Principal</a> &nbsp;&nbsp;&nbsp;
        <a href="${pageContext.request.contextPath}/admin/projetos/cadastro">Adicionar Projeto</a>
    </h2>
</div>

<div align="center">
    <table border="1">
        <caption>Lista de Projetos</caption>
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Data de Criação</th>
            <th>Membros</th>
            <th>Ações</th>
        </tr>
        <c:forEach var="projeto" items="${requestScope.listaProjetos}">
            <tr>
                <td>${projeto.id}</td>
                <td>${projeto.nome}</td>
                <td>${projeto.descricao}</td>
                <td>${projeto.dataCriacao}</td>
                <td>
                    <c:forEach var="membro" items="${projeto.membros}">
                        ${membro.nome}<br/>
                    </c:forEach>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/projetos/edicao?id=${projeto.id}">Editar</a>
                    &nbsp;&nbsp;
                    <a href="${pageContext.request.contextPath}/admin/projetos/remocao?id=${projeto.id}"
                       onclick="return confirm('Tem certeza de que deseja excluir este projeto?');">
                        Remover
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
