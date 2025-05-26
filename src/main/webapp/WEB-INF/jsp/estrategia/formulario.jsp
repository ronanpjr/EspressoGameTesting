<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>
    <c:choose>
      <c:when test="${not empty estrategia && estrategia.id > 0}">Editar</c:when>
      <c:otherwise>Nova</c:otherwise>
    </c:choose>
    Estratégia
  </title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; color: #333; }
    h1, h3 { color: #333; }
    nav { margin-bottom: 20px; }
    nav a { text-decoration: none; color: #007bff; margin-right: 15px; }
    nav a:hover { text-decoration: underline; }
    form div { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; font-weight: bold; }
    input[type="text"], input[type="url"], textarea, input[type="number"], input[type="file"] {
      width: 100%;
      padding: 10px;
      margin-top: 5px;
      border: 1px solid #ddd;
      border-radius: 4px;
      box-sizing: border-box;
    }
    textarea { min-height: 80px; resize: vertical; }
    .campo-grupo {
      border: 1px solid #e0e0e0;
      padding: 15px; /* Reduzido padding */
      margin-bottom: 15px; /* Reduzido margin */
      background-color: #fff;
      border-radius: 5px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05); /* Sombra mais suave */
    }
    .campo-grupo h4 { margin-top: 0; border-bottom: 1px solid #eee; padding-bottom: 8px; margin-bottom:12px; font-size: 1.1em;}
    .mensagem { padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px; }
    .erro { background-color: #f8d7da; border-color: #f5c6cb; color: #721c24; }
    .sucesso { background-color: #d4edda; border-color: #c3e6cb; color: #155724; }
    button, .btn {
      padding: 10px 15px;
      border-radius: 4px;
      border: none;
      cursor: pointer;
      font-size: 1em;
      margin-right: 10px;
      text-decoration: none;
      display: inline-block;
      text-align: center;
    }
    button[type="submit"] { background-color: #28a745; color: white; }
    button[type="submit"]:hover { background-color: #218838; }
    .btn-remover { background-color: #dc3545; color: white; padding: 5px 10px; font-size: 0.9em;}
    .btn-remover:hover { background-color: #c82333; }
    .btn-adicionar { background-color: #007bff; color: white; margin-top: 10px; }
    .btn-adicionar:hover { background-color: #0056b3; }
    .btn-cancelar { background-color: #6c757d; color: white; }
    .btn-cancelar:hover { background-color: #545b62; }
    hr { border: 0; height: 1px; background: #ddd; margin: 25px 0; } /* Margem ajustada */
    .imagem-atual-info { font-size: 0.85em; color: #555; margin-top: 5px; }
    .imagem-atual-info img { max-width: 100px; max-height: 70px; border: 1px solid #eee; margin-right: 5px; vertical-align: middle; }
  </style>
</head>
<body>
<h1>
  <c:choose>
    <c:when test="${not empty estrategia && estrategia.id > 0}">Editar Estratégia</c:when>
    <c:otherwise>Nova Estratégia</c:otherwise>
  </c:choose>
</h1>
<nav>
  <a href="${pageContext.request.contextPath}/estrategia?acao=listar">Voltar para Lista</a>
</nav>
<hr>

<c:if test="${not empty erroMsg}">
  <p class="mensagem erro"><c:out value="${erroMsg}"/></p>
</c:if>
<c:if test="${not empty erroMsgGeral}">
  <p class="mensagem erro"><c:out value="${erroMsgGeral}"/></p>
</c:if>

<form action="${pageContext.request.contextPath}/estrategia" method="post" id="estrategiaForm" enctype="multipart/form-data">
  <input type="hidden" name="acao" value="salvar">
  <c:if test="${not empty estrategia && estrategia.id > 0}">
    <input type="hidden" name="id_estrategia" value="${estrategia.id}">
  </c:if>

  <div>
    <label for="nome">Nome da Estratégia:</label>
    <input type="text" id="nome" name="nome" value="<c:out value='${estrategia.nome}'/>" required>
  </div>
  <div>
    <label for="descricao">Descrição:</label>
    <textarea id="descricao" name="descricao" required><c:out value='${estrategia.descricao}'/></textarea>
  </div>

  <hr>
  <h3>Exemplos</h3>
  <div id="exemplosContainer">
    <c:choose>
      <c:when test="${not empty estrategia.exemplos}">
        <c:forEach var="exemplo" items="${estrategia.exemplos}" varStatus="loopStatus">
          <div class="campo-grupo exemplo-item">
            <h4>Exemplo <span class="exemplo-numero">${loopStatus.count}</span></h4>
            <div>
              <label>Texto do Exemplo:</label>
              <textarea name="textoExemplo[]" placeholder="Descreva o exemplo aqui..."><c:out value="${exemplo.texto}"/></textarea>
            </div>
            <div>
              <label for="imagemExemplo_${loopStatus.index}">Imagem do Exemplo (opcional):</label>
              <input type="file" id="imagemExemplo_${loopStatus.index}" name="imagemExemplo[]" accept="image/png, image/jpeg, image/gif">
              <input type="hidden" name="urlImagemExemploAtual[]" value="${not empty exemplo.urlImagem ? exemplo.urlImagem : ''}">
              <c:if test="${not empty exemplo.urlImagem}">
                <div class="imagem-atual-info">
                  <img src="${pageContext.request.contextPath}/${applicationScope.uploadUrlPath}/${exemplo.urlImagem}" alt="Imagem atual">
                  (<c:out value="${exemplo.urlImagem}"/>)
                </div>
              </c:if>
            </div>
            <div>
              <label>Atributo Numérico (opcional):</label>
              <input type="number" name="atributo1Exemplo[]" value="${exemplo.atributo1}" placeholder="Ex: 123">
            </div>
            <button type="button" class="btn-remover remover-item">Remover Este Exemplo</button>
          </div>
        </c:forEach>
      </c:when>
    </c:choose>
  </div>
  <button type="button" id="addExemploBtn" class="btn-adicionar">Adicionar Novo Exemplo</button>

  <hr>
  <h3>Dicas</h3>
  <div id="dicasContainer">
    <c:choose>
      <c:when test="${not empty estrategia.dicas}">
        <c:forEach var="dica" items="${estrategia.dicas}" varStatus="loopStatus">
          <div class="campo-grupo dica-item">
            <h4>Dica <span class="dica-numero">${loopStatus.count}</span></h4>
            <div>
              <label>Texto da Dica:</label>
              <textarea name="dicaEstrategia[]" placeholder="Escreva a dica aqui..."><c:out value="${dica.dica}"/></textarea>
            </div>
            <button type="button" class="btn-remover remover-item">Remover Esta Dica</button>
          </div>
        </c:forEach>
      </c:when>
    </c:choose>
  </div>
  <button type="button" id="addDicaBtn" class="btn-adicionar">Adicionar Nova Dica</button>
  <hr>

  <div>
    <button type="submit">Salvar Estratégia</button>
    <a href="${pageContext.request.contextPath}/estrategia?acao=listar" class="btn btn-cancelar">Cancelar</a>
  </div>
</form>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const exemplosContainer = document.getElementById('exemplosContainer');
    const addExemploBtn = document.getElementById('addExemploBtn');
    const dicasContainer = document.getElementById('dicasContainer');
    const addDicaBtn = document.getElementById('addDicaBtn');

    function renumerarItens(container, itemClass, numeroClass, inputIdPrefixForFile) {
      const items = container.querySelectorAll('.' + itemClass);
      items.forEach((item, index) => {
        const numeroSpan = item.querySelector('.' + numeroClass);
        if(numeroSpan) numeroSpan.textContent = index + 1;

        if (inputIdPrefixForFile) { // Específico para exemplos com input de arquivo
          const fileInput = item.querySelector('input[type="file"]');
          if (fileInput) {
            fileInput.id = `${inputIdPrefixForFile}_${index}`;
            const label = item.querySelector(`label[for^="${inputIdPrefixForFile}"]`);
            if (label) {
              label.setAttribute('for', `${inputIdPrefixForFile}_${index}`);
            }
          }
        }
      });
    }

    addExemploBtn.addEventListener('click', function() {
      const novoIndex = exemplosContainer.querySelectorAll('.exemplo-item').length;
      const novoExemploHtml = `
            <div class="campo-grupo exemplo-item">
              <h4>Exemplo <span class="exemplo-numero">${novoIndex + 1}</span></h4>
              <div>
                <label>Texto do Exemplo:</label>
                <textarea name="textoExemplo[]" placeholder="Descreva o exemplo aqui..."></textarea>
              </div>
              <div>
                <label for="imagemExemplo_${novoIndex}">Imagem do Exemplo (opcional):</label>
                <input type="file" id="imagemExemplo_${novoIndex}" name="imagemExemplo[]" accept="image/png, image/jpeg, image/gif">
                <input type="hidden" name="urlImagemExemploAtual[]" value="">
              </div>
              <div>
                <label>Atributo Numérico (opcional):</label>
                <input type="number" name="atributo1Exemplo[]" placeholder="Ex: 123">
              </div>
              <button type="button" class="btn-remover remover-item">Remover Este Exemplo</button>
            </div>`;
      exemplosContainer.insertAdjacentHTML('beforeend', novoExemploHtml);
      renumerarItens(exemplosContainer, 'exemplo-item', 'exemplo-numero', 'imagemExemplo');
    });

    addDicaBtn.addEventListener('click', function() {
      const novoIndex = dicasContainer.querySelectorAll('.dica-item').length;
      const novaDicaHtml = `
            <div class="campo-grupo dica-item">
              <h4>Dica <span class="dica-numero">${novoIndex + 1}</span></h4>
              <div>
                <label>Texto da Dica:</label>
                <textarea name="dicaEstrategia[]" placeholder="Escreva a dica aqui..."></textarea>
              </div>
              <button type="button" class="btn-remover remover-item">Remover Esta Dica</button>
            </div>`;
      dicasContainer.insertAdjacentHTML('beforeend', novaDicaHtml);
      renumerarItens(dicasContainer, 'dica-item', 'dica-numero');
    });

    // Delegação de evento para botões de remover
    document.addEventListener('click', function(event) {
      if (event.target && event.target.classList.contains('remover-item')) {
        const itemParaRemover = event.target.closest('.campo-grupo');
        if (itemParaRemover) {
          const parentContainer = itemParaRemover.parentElement;
          itemParaRemover.remove();
          if (parentContainer.id === 'exemplosContainer') {
            renumerarItens(exemplosContainer, 'exemplo-item', 'exemplo-numero', 'imagemExemplo');
          } else if (parentContainer.id === 'dicasContainer') {
            renumerarItens(dicasContainer, 'dica-item', 'dica-numero');
          }
        }
      }
    });

    // Renumerar itens existentes na carga da página (para edição)
    renumerarItens(exemplosContainer, 'exemplo-item', 'exemplo-numero', 'imagemExemplo');
    renumerarItens(dicasContainer, 'dica-item', 'dica-numero');

    // Adicionar um item inicial se for um formulário novo e não houver nenhum
    // (Checa se NÃO existe input com nome id_estrategia, o que indica novo cadastro)
    if (document.querySelector('input[name="id_estrategia"]') === null) {
      if (exemplosContainer.children.length === 0) {
        addExemploBtn.click();
      }
      if (dicasContainer.children.length === 0) {
        addDicaBtn.click();
      }
    }
  });
</script>
</body>
</html>