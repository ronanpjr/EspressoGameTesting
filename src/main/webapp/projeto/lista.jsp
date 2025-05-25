<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<html>
<head>
    <title><fmt:message key="project.management.title" /></title>
    <link href="${pageContext.request.contextPath}/lista_layout.css" rel="stylesheet" type="text/css"/>
    <style>
        .lang-switcher {
            text-align: right;
            margin-bottom: 10px;
            padding: 5px;
        }
        .lang-switcher a, .lang-switcher span {
            display: inline-block;
            padding: 5px 10px;
            margin-left: 5px;
            border: 1px solid #6d4c41;
            border-radius: 5px;
            text-decoration: none;
            color: #6d4c41;
            background-color: #fff8e1;
            font-size: 0.9em;
        }
        .lang-switcher a:hover {
            background-color: #d7ccc8;
        }
        .lang-switcher span {
            background-color: #6d4c41;
            color: white;
            font-weight: bold;
        }
    </style>
</head>
<body>

<div align="center">
    <h1><fmt:message key="project.management.title" /></h1>
    <h2>
        <a href="${pageContext.request.contextPath}/logado/usuario/main.jsp">
            <fmt:message key="link.main_menu" />
        </a>
        &nbsp;&nbsp;&nbsp;
        <c:if test="${sessionScope.usuarioLogado.papel == 'admin'}">
            <a href="${pageContext.request.contextPath}/admin/projetos/cadastro">
                <fmt:message key="link.project.add" />
            </a>
        </c:if>
    </h2>
</div>

<div align="center">
    <table border="1">
        <caption><fmt:message key="project.list.title" /></caption>
        <tr>
            <th><fmt:message key="project.table.id" /></th>
            <th><fmt:message key="project.table.name" /></th>
            <th><fmt:message key="project.table.description" /></th>
            <th><fmt:message key="project.table.creation_date" /></th>
            <th><fmt:message key="project.table.members" /></th>
            <th><fmt:message key="project.table.actions" /></th>
        </tr>
        <fmt:message key="confirm.project.delete" var="confirmMsg" />

        <c:forEach var="projeto" items="${requestScope.listaProjetos}">
            <tr>
                <td>${projeto.id}</td>
                <td><c:out value="${projeto.nome}"/></td>
                <td><c:out value="${projeto.descricao}"/></td>
                <td><fmt:formatDate value="${projeto.dataCriacao}" pattern="dd/MM/yyyy" /></td>
                <td>
                    <c:forEach var="membro" items="${projeto.membros}">
                        <c:out value="${membro.nome}" /><br/>
                    </c:forEach>
                </td>
                <td>
                    <c:if test="${sessionScope.usuarioLogado.papel == 'admin'}">
                        <a href="${pageContext.request.contextPath}/admin/projetos/edicao?id=${projeto.id}">
                            <fmt:message key="button.edit" />
                        </a>
                        &nbsp;&nbsp;
                        <a href="${pageContext.request.contextPath}/admin/projetos/remocao?id=${projeto.id}"
                           onclick="return confirm('${confirmMsg}');">
                            <fmt:message key="button.remove" />
                        </a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
