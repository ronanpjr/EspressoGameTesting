<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'pt_BR'}" />
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="pagina.erro.titulo" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <style>
        .erro-container {
            width: auto; max-width: 700px; text-align: left; margin: 20px auto;
            padding:15px; border: 1px solid #d32f2f; background-color: #ef9a9a;
            color: #b71c1c; border-radius: 8px;
        }
        .erro-container ul { list-style-position: inside; padding-left:0; margin-top: 5px; }
        .erro-container li { margin-bottom: 5px; }
        .stacktrace {
            text-align: left; white-space: pre-wrap; word-wrap: break-word;
            background-color: #f0f0f0; border: 1px solid #ccc;
            padding: 10px; margin-top:10px; max-height: 300px;
            overflow-y: auto; font-family: monospace; font-size: 0.9em;
        }
    </style>
</head>
<body>
<center>
    <h1><fmt:message key="pagina.erro.cabecalho" /></h1>

    <c:if test="${requestScope.mensagens != null && requestScope.mensagens.isExisteErros()}">
        <div class="erro-container">
            <strong><fmt:message key="pagina.erro.ocorreramOsSeguintesErros" />:</strong>
            <ul>
                <c:forEach var="erroKey" items="${requestScope.mensagens.getErros()}">
                    <li>
                        <fmt:message key="${erroKey}">
                            <c:out value="(!) ${erroKey}" />
                        </fmt:message>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <c:if test="${pageContext.exception != null && (requestScope.mensagens == null || !requestScope.mensagens.isExisteErros())}">
        <h2><fmt:message key="pagina.erro.detalheExcecao" />:</h2>
        <div class="erro-container" style="text-align:left;">
            <p><b>Tipo:</b> <c:out value="${pageContext.exception.getClass().getName()}" /></p>
            <p><b>Mensagem:</b> <c:out value="${pageContext.exception.getMessage() != null ? pageContext.exception.getMessage() : 'Sem mensagem detalhada da exceção.'}" /></p>

            <p><strong>Stack Trace (para depuração):</strong></p>
            <div class="stacktrace">
                    <%-- Cuidado ao exibir stack trace em produção --%>
                <% pageContext.getException().printStackTrace(new java.io.PrintWriter(out)); %>
            </div>
        </div>
    </c:if>

    <%-- 3. Mensagem genérica se nenhuma das anteriores se aplicar --%>
    <c:if test="${(requestScope.mensagens == null || !requestScope.mensagens.isExisteErros()) && pageContext.exception == null}">
        <div class="erro-container" style="text-align:center;">
            <p><fmt:message key="pagina.erro.generica" /></p>
        </div>
    </c:if>

    <br/>
    <p>
        <a href="${pageContext.request.contextPath}/usuario/"><fmt:message key="link.main_menu"/></a> |
        <a href="javascript:history.back()"><fmt:message key="comum.voltarPaginaAnterior"/></a>
    </p>
</center>
</body>
</html>