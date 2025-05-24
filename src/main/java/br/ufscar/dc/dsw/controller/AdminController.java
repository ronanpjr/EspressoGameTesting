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
    private AdminDAO adminDAO;

    public void init() {
        adminDAO = new AdminDAO();
    }

    protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
            throws ServletException, IOException {
        String acao = requisicao.getParameter("action");
        try {
            switch (acao == null ? "listar" : acao) {
                case "novo":
                    mostrarFormulario(requisicao, resposta);
                    break;
                case "inserir":
                    inserirAdmin(requisicao, resposta);
                    break;
                case "editar":
                    mostrarFormularioEdicao(requisicao, resposta);
                    break;
                case "atualizar":
                    atualizarAdmin(requisicao, resposta);
                    break;
                case "remover":
                    removerAdmin(requisicao, resposta);
                    break;
                default:
                    listarAdmins(requisicao, resposta);
                    break;
            }
        } catch (Exception ex) {
            requisicao.setAttribute("mensagemErro", ex.getMessage());
            requisicao.getRequestDispatcher("erro.jsp").forward(requisicao, resposta);
        }
    }

    private void listarAdmins(HttpServletRequest requisicao, HttpServletResponse resposta)
            throws SQLException, ServletException, IOException {
        List<Usuario> lista = adminDAO.listarTodos();
        requisicao.setAttribute("admins", lista);
        requisicao.getRequestDispatcher("admin/list.jsp").forward(requisicao, resposta);
    }

    private void mostrarFormulario(HttpServletRequest requisicao, HttpServletResponse resposta)
            throws ServletException, IOException {
        requisicao.setAttribute("admin", new Usuario());
        requisicao.getRequestDispatcher("admin/form.jsp").forward(requisicao, resposta);
    }

    private void inserirAdmin(HttpServletRequest requisicao, HttpServletResponse resposta)
            throws SQLException, IOException {
        String nome = requisicao.getParameter("name");
        String login = requisicao.getParameter("email");
        String senha = requisicao.getParameter("password");

        Usuario admin = new Usuario(0L, nome, login, senha, "admin");
        adminDAO.inserir(admin);
        resposta.sendRedirect("admin");
    }

    private void mostrarFormularioEdicao(HttpServletRequest requisicao, HttpServletResponse resposta)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(requisicao.getParameter("id"));
        Usuario admin = adminDAO.buscarPorId(id);
        requisicao.setAttribute("admin", admin);
        requisicao.getRequestDispatcher("admin/form.jsp").forward(requisicao, resposta);
    }

    private void atualizarAdmin(HttpServletRequest requisicao, HttpServletResponse resposta)
            throws SQLException, IOException {
        int id = Integer.parseInt(requisicao.getParameter("id"));
        String nome = requisicao.getParameter("name");
        String login = requisicao.getParameter("email");
        String senha = requisicao.getParameter("password");


        Usuario admin = new Usuario(0L, nome, login, senha, "admin");
        adminDAO.atualizar(admin);
        resposta.sendRedirect("admin");
    }

    private void removerAdmin(HttpServletRequest requisicao, HttpServletResponse resposta)
            throws SQLException, IOException {
        int id = Integer.parseInt(requisicao.getParameter("id"));
        adminDAO.remover(id);
        resposta.sendRedirect("admin");
    }
}
