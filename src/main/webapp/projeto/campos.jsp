<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<table border="1">
    <caption>
        <c:choose>
            <c:when test="${projeto != null}">
                Edição
            </c:when>
            <c:otherwise>
                Cadastro
            </c:otherwise>
        </c:choose>
    </caption>
    <c:if test="${projeto != null}">
        <input type="hidden" name="id" value="${projeto.id}" />
    </c:if>
    <tr>
        <td><label for="nome">Nome</label></td>
        <td><input type="text" id="nome" name="nome" size="45" required value="${projeto.nome}" /></td>
    </tr>
    <tr>
        <td><label for="descricao">Descrição</label></td>
        <td><textarea id="descricao" name="descricao" rows="4" cols="45">${projeto.descricao}</textarea></td>
    </tr>
    <tr>
        <td><label for="membros">Membros Permitidos (e-mails separados por vírgula)</label></td>
        <c:set var="membrosLogin" value="" />
        <c:forEach var="m" items="${projeto.membros}" varStatus="status">
            <c:set var="membrosLogin" value="${membrosLogin}${m.login}${status.last ? '' : ', '}" />
        </c:forEach>
        <input type="text" id="membros" name="membros" size="60" value="${membrosLogin}" />

    </tr>
    <tr>
        <td colspan="2" align="center"><input type="submit" value="Salvar" /></td>
    </tr>
</table>