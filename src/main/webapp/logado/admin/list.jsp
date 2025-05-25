<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Admins</title></head>
<body>
<h2>Lista de Admins</h2>
<a href="admin?action=new">Novo Admin</a>
<table border="1">
    <tr><th>ID</th><th>Nome</th><th>Email</th><th>Ações</th></tr>
    <c:forEach var="a" items="${admins}">
        <tr>
            <td>${a.id}</td>
            <td>${a.name}</td>
            <td>${a.email}</td>
            <td>
                <a href="admin?action=edit&id=${a.id}">Editar</a>
                <a href="admin?action=delete&id=${a.id}">Excluir</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>