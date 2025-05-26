package br.ufscar.dc.dsw.controller;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.dao.AdminDAO;
import br.ufscar.dc.dsw.util.Erro;

import java.sql.SQLException;
import java.util.List;

/*
@WebServlet(urlPatterns = "/admin/*")
public class AdminController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();
        if (usuario == null) {
            response.sendRedirect(request.getContextPath());
        } else if (usuario.getPapel().equals("admin")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/index.jsp");
            dispatcher.forward(request, response);
        } else {
            erros.add("Acesso não autorizado!");
            erros.add("Apenas Papel [admin] tem acesso a essa página");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
        }
    }
}
*/



@WebServlet(urlPatterns = "/admin/*")
public class AdminController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private AdminDAO adminDAO;

    public void init() {
        adminDAO = new AdminDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();
        if (usuario == null) {
            response.sendRedirect(request.getContextPath());
        } else if (usuario.getPapel().equals("admin")) {
            try {
                listarAdmins(request, response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            erros.add("Acesso não autorizado!");
            erros.add("Apenas Papel [admin] tem acesso a essa página");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
        }

        String acao = request.getParameter("action");
        try {
            switch (acao == null ? "listar" : acao) {
                case "novo":
                    mostrarFormulario(request, response);
                    break;
                case "inserir":
                    inserirAdmin(request, response);
                    break;
                case "editar":
                    mostrarFormularioEdicao(request, response);
                    break;
                case "atualizar":
                    atualizarAdmin(request, response);
                    break;
                case "remover":
                    removerAdmin(request, response);
                    break;
                default:
                    listarAdmins(request, response);
                    break;
            }
        } catch (Exception ex) {
            request.setAttribute("mensagemErro", ex.getMessage());
            request.getRequestDispatcher("erro.jsp").forward(request, response);
        }
    }

    private void listarAdmins(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Usuario> lista = adminDAO.listarTodos();
        request.setAttribute("admins", lista);
        request.getRequestDispatcher("/logado/admin/list.jsp").forward(request, response);
    }

    private void  mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("admin", new Usuario());
        request.getRequestDispatcher("/logado/admin/form.jsp").forward(request, response);
    }

    private void inserirAdmin(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String nome = request.getParameter("name");
        String login = request.getParameter("email");
        String senha = request.getParameter("password");

        Usuario admin = new Usuario(0L, nome, login, senha, "admin");
        adminDAO.inserir(admin);
        response.sendRedirect("admin");
    }

    private void mostrarFormularioEdicao(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Usuario admin = adminDAO.buscarPorId(id);
        request.setAttribute("admin", admin);
        request.getRequestDispatcher("/logado/admin/form.jsp").forward(request, response);
    }

    private void atualizarAdmin(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String nome = request.getParameter("name");
        String login = request.getParameter("email");
        String senha = request.getParameter("password");


        Usuario admin = new Usuario(0L, nome, login, senha, "admin");
        adminDAO.atualizar(admin);
        response.sendRedirect("lista");
    }

    private void removerAdmin(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        adminDAO.remover(id);
        response.sendRedirect("lista");
    }
}
