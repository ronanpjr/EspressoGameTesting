package br.ufscar.dc.dsw.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.util.Erro;

@WebServlet(urlPatterns = "/usuario/*")
public class UsuarioController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UsuarioDAO dao;

    @Override
    public void init() {
        dao = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();

        // Check if user is authenticated
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp"); // Redireciona para o login
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        if (!action.equals("") && !action.equals("/") && !(usuarioLogado.getPapel().equalsIgnoreCase("tester") || usuarioLogado.getPapel().equalsIgnoreCase("admin"))) {
            erros.add("error.unauthorized.title");
            erros.add("error.unauthorized.tester_admin");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
            return;
        }

        try {
            switch (action) {
                case "/cadastro":
                    if (ehAdmin(usuarioLogado, erros)) {
                        apresentaFormCadastro(request, response);
                    } else {
                        erroNaoAutorizado(request, response, erros);
                    }
                    break;
                case "/insercao":
                    if (ehAdmin(usuarioLogado, erros)) {
                        insere(request, response);
                    } else {
                        erroNaoAutorizado(request, response, erros);
                    }
                    break;
                case "/remocao":
                    if (ehAdmin(usuarioLogado, erros)) {
                        remove(request, response);
                    } else {
                        erroNaoAutorizado(request, response, erros);
                    }
                    break;
                case "/edicao":
                    if (ehAdmin(usuarioLogado, erros)) {
                        apresentaFormEdicao(request, response);
                    } else {
                        erroNaoAutorizado(request, response, erros);
                    }
                    break;
                case "/atualizacao":
                    if (ehAdmin(usuarioLogado, erros)) {
                        atualize(request, response);
                    } else {
                        erroNaoAutorizado(request, response, erros);
                    }
                    break;
                case "/lista":
                    lista(request, response); // Lista pode ser vista por admin e tester
                    break;
                default:
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/usuario/main.jsp");
                    dispatcher.forward(request, response);

            }
        } catch (RuntimeException | IOException | ServletException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private boolean ehAdmin(Usuario usuarioLogado, Erro erros) {
        if (usuarioLogado != null && usuarioLogado.getPapel().equalsIgnoreCase("admin")) {
            return true;
        } else {
            erros.add("error.unauthorized.title"); // Usa chave
            erros.add("error.unauthorized.admin_only"); // Usa chave específica para admin
            return false;
        }
    }

    private void erroNaoAutorizado(HttpServletRequest request, HttpServletResponse response, Erro erros) throws ServletException, IOException {
        request.setAttribute("mensagens", erros);
        RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
        rd.forward(request, response);
    }


    private void lista(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Usuario> listaUsuarios = dao.getAll();
        request.setAttribute("listaUsuarios", listaUsuarios);
        request.setAttribute("contextPath", request.getContextPath().replace("/", ""));
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/usuario/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/usuario/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormEdicao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Usuario usuario = dao.getbyID(id);
        request.setAttribute("usuario", usuario);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/usuario/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void insere(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String login = request.getParameter("login");
        String senha = request.getParameter("senha");
        String papel = request.getParameter("papel");

        // Adicionar validação aqui e adicionar chaves de erro se necessário
        // Ex: if (nome == null || nome.isEmpty()) erros.add("error.user.name.required");

        Usuario usuario = new Usuario(nome, login, senha, papel);
        dao.insert(usuario);
        response.sendRedirect("lista");
    }

    private void atualize(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Long id = Long.parseLong(request.getParameter("id"));
        String nome = request.getParameter("nome");
        String login = request.getParameter("login");
        String senha = request.getParameter("senha");
        String papel = request.getParameter("papel");


        Usuario usuario = new Usuario(id, nome, login, senha, papel);
        dao.update(usuario);
        response.sendRedirect("lista");
    }

    private void remove(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));

        Usuario usuario = new Usuario(id);
        dao.delete(usuario);
        response.sendRedirect("lista");
    }
}