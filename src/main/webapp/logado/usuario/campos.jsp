<%--
  Created by IntelliJ IDEA.
  User: ronan
  Date: 09/05/2025
  Time: 19:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<head>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
</head>
<table border="1">
    <caption>
        <c:choose>
            <c:when test="${usuario != null}">
                Edição
            </c:when>
            <c:otherwise>
                Cadastro
            </c:otherwise>
        </c:choose>
    </caption>
    <c:if test="${usuario != null}">
        <input type="hidden" name="id" value="${usuario.id}" />
    </c:if>
    <tr>
        <td><label for="nome">Nome</label></td>
        <td><input type="text" id="nome" name="nome" size="45"
                   required value="${usuario.nome}" /></td>
    </tr>
    <tr>
        <td><label for="login">Login</label></td>
        <td><input type="text" id="login" name="login" size="45" required
                   value="${usuario.login}" /></td>
    </tr>
    <tr>
        <td><label for="senha">Senha</label></td>
        <td><input type="password" id="senha" name="senha" size="45" required
                   value="${usuario.senha}" /></td>
    </tr>
    <tr>
        <td><label for="papel">Papel</label></td>
        <td>
            <select id="papel" name="papel">
                <option value="admin" ${usuario.papel == 'admin' ? 'selected' : ''}>Administrador</option>
                <option value="user" ${usuario.papel == 'user' ? 'selected' : ''}>Usuário</option>
                <option value="tester" ${usuario.papel == 'tester' ? 'selected' : ''}>Testador</option>
            </select>
        </td>
    </tr>
    <tr>
        <td colspan="2" align="center"><input type="submit" value="Salva" /></td>
    </tr>
</table>