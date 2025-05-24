package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.ProjetoDAO;
import br.ufscar.dc.dsw.dao.UsuarioDAO.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Usuario;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(urlPatterns = "/admin/projetos/*")
public class ProjetoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO;



    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null || !"ADMIN".equals(usuario.getPapel())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null || !"ADMIN".equals(usuario.getPapel())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "/cadastro":
                    apresentaFormCadastro(request, response);
                    break;
                case "/insercao":
                    insere(request, response);
                    break;
                case "/remocao":
                    remove(request, response);
                    break;
                case "/edicao":
                    apresentaFormEdicao(request, response);
                    break;
                case "/atualizacao":
                    atualize(request, response);
                    break;
                default:
                    lista(request, response);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            throw new ServletException(e);
        }
    }
    private String montaStringLogins(List<Usuario> membros) {
        if (membros == null || membros.isEmpty()) {
            return "vazio";
        }
        StringBuilder sb = new StringBuilder();
        for (Usuario u : membros) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(u.getLogin());
        }
        return sb.toString();
    }

    private void lista(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Projeto> listaProjetos = projetoDAO.getAll();
        request.setAttribute("listaProjetos", listaProjetos);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/projeto/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Usuario> usuarios = usuarioDAO.getAll();
        request.setAttribute("usuarios", usuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/projeto/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormEdicao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Projeto projeto = projetoDAO.get(id);
        List<Usuario> usuarios = usuarioDAO.getAll();
        request.setAttribute("usuarios", usuarios);
        request.setAttribute("projeto", projeto);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/projeto/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void insere(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String membrosParam = request.getParameter("membros");
        Date dataCriacao = new Date();

        List<Usuario> membros = new ArrayList<>();
        List<String> mensagens = new ArrayList<>();

        // Validação de campos obrigatórios
        if (nome == null || nome.trim().isEmpty()) {
            mensagens.add("O campo Nome é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            mensagens.add("O campo Descrição é obrigatório.");
        }

        if (membrosParam != null && !membrosParam.trim().isEmpty()) {
            String[] logins = membrosParam.split(",");
            for (String login : logins) {
                login = login.trim();
                if (!login.isEmpty()) {
                    Usuario usuario = usuarioDAO.getbyLogin(login);
                    if (usuario != null) {
                        membros.add(usuario);
                    } else {
                        mensagens.add("Usuário com login '" + login + "' não encontrado.");
                    }
                }
            }
        } else {
            mensagens.add("É necessário informar ao menos um membro válido.");
        }

        if (!mensagens.isEmpty()) {
            Projeto projetoErro = new Projeto(nome, descricao, dataCriacao, membros);
            request.setAttribute("mensagens", mensagens);
            request.setAttribute("projeto", projetoErro);
            apresentaFormCadastro(request, response);
            return;
        }

        Projeto projeto = new Projeto(nome, descricao, dataCriacao, membros);
        projetoDAO.insert(projeto);
        response.sendRedirect("lista");
    }



    private void atualize(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Long id = Long.parseLong(request.getParameter("id"));
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String membrosParam = request.getParameter("membros");

        List<Usuario> membros = new ArrayList<>();
        List<String> mensagens = new ArrayList<>();

        if (nome == null || nome.trim().isEmpty()) {
            mensagens.add("O campo Nome é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            mensagens.add("O campo Descrição é obrigatório.");
        }

        if (membrosParam != null && !membrosParam.trim().isEmpty()) {
            String[] logins = membrosParam.split(",");
            for (String login : logins) {
                login = login.trim();
                if (!login.isEmpty()) {
                    Usuario usuario = usuarioDAO.getbyLogin(login);
                    if (usuario != null) {
                        membros.add(usuario);
                    } else {
                        mensagens.add("Usuário com login '" + login + "' não encontrado.");
                    }
                }
            }
        } else {
            mensagens.add("É necessário informar ao menos um membro válido.");
        }

        if (!mensagens.isEmpty()) {
            Projeto projetoErro = new Projeto(nome, descricao, new Date(), membros, id);
            request.setAttribute("mensagens", mensagens);
            request.setAttribute("projeto", projetoErro);
            apresentaFormEdicao(request, response);
            return;
        }

        Projeto projeto = new Projeto(nome, descricao, new Date(), membros, id);
        projetoDAO.update(projeto);
        response.sendRedirect("lista");
    }






    private void remove(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Projeto projeto = new Projeto(id);
        projetoDAO.delete(projeto);
        response.sendRedirect("lista");
    }
}
