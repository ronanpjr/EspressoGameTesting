<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Sistema de Projetos</title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .form-container {
            width: 60%;
            margin: 0 auto;
            text-align: center;
        }
        table {
            margin: 0 auto;
            border-collapse: collapse;
        }
        td {
            padding: 8px;
        }
        input[type="text"], textarea {
            width: 100%;
        }
    </style>
</head>
<body>

<div class="form-container">
    <h1>Gerenciamento de Projetos</h1>
    <h2><a href="lista">Lista de Projetos</a></h2>

    <c:set var="logins" value="" />
    <c:forEach var="m" items="${projeto.membros}" varStatus="status">
        <c:set var="logins" value="${logins}${m.login}${!status.last ? ', ' : ''}" />
    </c:forEach>

    <c:choose>
        <c:when test="${projeto != null && projeto.id != null}">
            <form action="atualizacao" method="post">
                <input type="hidden" name="id" value="${projeto.id}" />
        </c:when>
        <c:otherwise>
            <form action="insercao" method="post">
        </c:otherwise>
    </c:choose>

        <table border="1" style="margin: 20px auto; width: 500px;">
            <caption>
                <c:choose>
                    <c:when test="${projeto != null && projeto.id != null}">Edição</c:when>
                    <c:otherwise>Cadastro</c:otherwise>
                </c:choose>
            </caption>
            <tr>
                <td><label for="nome">Nome</label></td>
                <td><input type="text" id="nome" name="nome" value="${projeto.nome}" required /></td>
            </tr>
            <tr>
                <td><label for="descricao">Descrição</label></td>
                <td><textarea id="descricao" name="descricao">${projeto.descricao}</textarea></td>
            </tr>
            <tr>
                <td><label for="membros">Membros Permitidos (logins separados por vírgula)</label></td>
                <td><input type="text" id="membros" name="membros" value="${logins}" style="width: 100%;" /></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align:center;">
                    <input type="submit" value="Salvar" />
                </td>
            </tr>
        </table>
    </form>

    <c:if test="${!empty requestScope.mensagens}">
        <ul style="color: red;">
            <c:forEach items="${requestScope.mensagens}" var="mensagem">
                <li>${mensagem}</li>
            </c:forEach>
        </ul>
    </c:if>
</div>

</body>
</html>
