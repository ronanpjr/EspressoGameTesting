package br.ufscar.dc.dsw.controller;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.util.Erro;

@WebServlet(name = "Index", urlPatterns = { "/index.jsp", "/logout.jsp" })
public class IndexController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Erro erros = new Erro();

        String lang = request.getParameter("lang");
        if (lang != null && !lang.isEmpty()) {
            request.getSession().setAttribute("lang", lang);
            String queryString = request.getQueryString();
            queryString = queryString != null ? queryString.replaceAll("[&?]lang=[^&]*", "") : "";
            String redirectUrl = request.getRequestURI() + (queryString.isEmpty() ? "" : "?" + queryString);
            response.sendRedirect(redirectUrl.isEmpty() ? request.getContextPath() + "/" : redirectUrl);
            return;
        }

        if (request.getParameter("bOK") != null) {
            String login = request.getParameter("login");
            String senha = request.getParameter("senha");
            if (login == null || login.isEmpty()) {
                erros.add("login.error.required.user");
            }
            if (senha == null || senha.isEmpty()) {
                erros.add("login.error.required.password");
            }
            if (!erros.isExisteErros()) {
                UsuarioDAO dao = new UsuarioDAO();
                Usuario usuario = dao.getbyLogin(login);
                if (usuario != null) {
                    if (usuario.getSenha().equals(senha)) {
                        request.getSession().setAttribute("usuarioLogado", usuario);
                        if (request.getSession().getAttribute("lang") == null) {
                            request.getSession().setAttribute("lang", "pt_BR");
                        }

                        if (usuario.getPapel().equalsIgnoreCase("ADMIN")) {
                            response.sendRedirect("admin/");
                        } else {
                            response.sendRedirect("usuario/");
                        }
                        return;
                    } else {
                        erros.add("login.error.invalid");
                    }
                } else {
                    erros.add("login.error.notfound");
                }
            }
        } else {
            String path = request.getRequestURI().substring(request.getContextPath().length());
            if (path.contains("logout.jsp")) {
                request.getSession().invalidate();
            }
        }

        request.setAttribute("mensagens", erros);
        String URL = "/login.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(URL);
        rd.forward(request, response);
    }
}