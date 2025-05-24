<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="admin" class="model.User" scope="request" />
<html>
<head><title>Admin</title></head>
<body>
<h2><c:choose><c:when test="${admin.id == 0}">Novo Admin</c:when><c:otherwise>Editar Admin</c:otherwise></c:choose></h2>
<form action="admin" method="get">
    <input type="hidden" name="action" value="${admin.id == 0 ? 'insert' : 'update'}" />
    <input type="hidden" name="id" value="${admin.id}" />
    Nome: <input type="text" name="name" value="${admin.name}" /><br/>
    Email: <input type="email" name="email" value="${admin.email}" /><br/>
    Senha: <input type="password" name="password" value="${admin.password}" /><br/>
    <input type="submit" value="Salvar" />
</form>
</body>
</html>