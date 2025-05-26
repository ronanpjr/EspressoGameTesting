<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalhes da Estratégia - <c:out value="${estrategia.nome}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/layout.css">
</head>
<body>
<div class="container">
    <h1>Detalhes da Estratégia</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/estrategia?acao=listar">Voltar para Lista</a>
        <c:if test="${not empty estrategia}">
            | <a href="${pageContext.request.contextPath}/estrategia?acao=editar&id=${estrategia.id}">Editar Esta Estratégia</a>
        </c:if>
    </nav>

    <c:if test="${not empty param.erro and param.erro == 'nao_encontrado_detalhes'}">
        <p class="mensagem erro">Estratégia não encontrada.</p>
    </c:if>
    <c:if test="${not empty erroMsgGeral}">
        <p class="mensagem erro"><c:out value="${erroMsgGeral}"/></p>
    </c:if>


    <c:if test="${not empty estrategia}">
        <div class="detalhe-item">
            <strong>ID:</strong> <c:out value="${estrategia.id}"/>
        </div>
        <div class="detalhe-item">
            <strong>Nome:</strong> <c:out value="${estrategia.nome}"/>
        </div>
        <div class="detalhe-item">
            <strong>Descrição:</strong>
            <div class="descricao-text"><c:out value="${estrategia.descricao}"/></div>
        </div>

        <div class="secao">
            <h3>Exemplos:</h3>
            <c:choose>
                <c:when test="${not empty estrategia.exemplos}">
                    <ul>
                        <c:forEach var="exemplo" items="${estrategia.exemplos}">
                            <li class="item-lista">
                                <strong>Texto:</strong> <div style="white-space: pre-wrap; margin-top: 3px;"><c:out value="${exemplo.texto}"/></div>
                                <c:if test="${exemplo.atributo1 != null}">
                                    <br><strong>Atributo Numérico:</strong> <c:out value="${exemplo.atributo1}"/>
                                </c:if>
                                <c:if test="${not empty exemplo.urlImagem}">
                                    <br><strong>Imagem:</strong>
                                    <c:url var="imageUrl" value="/${applicationScope.uploadUrlPath}/${exemplo.urlImagem}" />
                                    <a href="${imageUrl}" target="_blank" title="Ver imagem: ${exemplo.urlImagem}">
                                        <img src="${imageUrl}" alt="Imagem do exemplo: <c:out value='${exemplo.texto}'/>" class="exemplo-imagem">
                                    </a>
                                </c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p class="sem-itens">Nenhum exemplo cadastrado para esta estratégia.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="secao">
            <h3>Dicas:</h3>
            <c:choose>
                <c:when test="${not empty estrategia.dicas}">
                    <ul>
                        <c:forEach var="dica" items="${estrategia.dicas}">
                            <li class="item-lista"><div style="white-space: pre-wrap;"><c:out value="${dica.dica}"/></div></li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p class="sem-itens">Nenhuma dica cadastrada para esta estratégia.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>
</div>
</body>
</html>