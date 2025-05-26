package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.BugDAO;
import br.ufscar.dc.dsw.dao.SessaoTesteDAO;
import br.ufscar.dc.dsw.domain.Bug;
import br.ufscar.dc.dsw.domain.SessaoTeste;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.util.Erro;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(urlPatterns = "/bugs/*")
public class BugController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private BugDAO bugDAO;
    private SessaoTesteDAO sessaoTesteDAO;

    @Override
    public void init() {
        bugDAO = new BugDAO();
        sessaoTesteDAO = new SessaoTesteDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();
        request.setAttribute("mensagens", erros);
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        String action = request.getPathInfo();
        if (action == null) {
            action = "/";
        }

        Erro flashErros = (Erro) request.getSession().getAttribute("mensagensFlash");
        if (flashErros != null) {
            erros.getErros().addAll(flashErros.getErros());
            request.getSession().removeAttribute("mensagensFlash");
        }

        try {
            switch (action) {
                case "/cadastro":
                    apresentaFormCadastroBug(request, response, erros, usuarioLogado);
                    break;
                case "/insercao":
                    insereBug(request, response, erros, usuarioLogado);
                    break;
                case "/edicao":
                    apresentaFormEdicaoBug(request, response, erros, usuarioLogado);
                    break;
                case "/atualizacao":
                    atualizaBug(request, response, erros, usuarioLogado);
                    break;
                case "/remocao":
                    removeBug(request, response, erros, usuarioLogado);
                    break;
                case "/lista-sessao":
                    listaBugsPorSessao(request, response, erros, usuarioLogado);
                    break;
                default:
                    erros.add("bug.erro.acaoNaoEncontrada");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
                    dispatcher.forward(request, response);
                    break;
            }
        } catch (Exception e) {
            erros.add("Erro interno do sistema (BugController): " + e.getMessage());
            e.printStackTrace(); // Log full stack trace for debugging
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
        }
    }

    private boolean isTester(Usuario u) {
        return u != null && "tester".equalsIgnoreCase(u.getPapel());
    }

    private boolean isAdmin(Usuario u) {
        return u != null && "admin".equalsIgnoreCase(u.getPapel());
    }

    private void acessoNegado(HttpServletRequest request, HttpServletResponse response, Erro erros, String chaveMensagem)
            throws ServletException, IOException {
        if (!erros.isExisteErros()) { // Add message only if no other specific error is present
            erros.add(chaveMensagem);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/noAuth.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormCadastroBug(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idSessaoParam = request.getParameter("idSessao");
        Long idSessao;

        if (idSessaoParam == null || idSessaoParam.isEmpty()) {
            erros.add("bug.erro.idSessaoObrigatorio");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp"); // Or redirect to a relevant page
            dispatcher.forward(request, response);
            return;
        }

        try {
            idSessao = Long.parseLong(idSessaoParam);
        } catch (NumberFormatException e) {
            erros.add("bug.erro.idSessaoInvalido");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        SessaoTeste sessao = sessaoTesteDAO.getById(idSessao);
        if (sessao == null) {
            erros.add("sessao.erro.sessaoNaoEncontrada"); // Reusing session error key
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Authorization: Only tester of the session or admin can add bugs
        if (!isAdmin(usuarioLogado) && !(isTester(usuarioLogado) && usuarioLogado.getId().equals(sessao.getIdTester()))) {
            acessoNegado(request, response, erros, "error.unauthorized.addBug");
            return;
        }

        request.setAttribute("sessao", sessao); // Pass session to context for the form
        request.setAttribute("idSessao", idSessao); // Explicitly pass idSessao
        request.setAttribute("isEditModeBug", false);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/bug/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void insereBug(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String idSessaoParam = request.getParameter("idSessao");
        String descricao = request.getParameter("descricao");
        Long idSessao = null;

        try {
            if (idSessaoParam == null || idSessaoParam.isEmpty()) {
                erros.add("bug.erro.idSessaoObrigatorio");
            } else {
                try {
                    idSessao = Long.parseLong(idSessaoParam);
                } catch (NumberFormatException e) {
                    erros.add("bug.erro.idSessaoInvalido");
                }
            }

            if (descricao == null || descricao.trim().isEmpty()) {
                erros.add("bug.erro.descricaoObrigatoria");
            }

            SessaoTeste sessao = null;
            if (idSessao != null) {
                sessao = sessaoTesteDAO.getById(idSessao);
                if (sessao == null) {
                    erros.add("sessao.erro.sessaoNaoEncontrada");
                } else if (!isAdmin(usuarioLogado) && !(isTester(usuarioLogado) && usuarioLogado.getId().equals(sessao.getIdTester()))) {
                    // Authorization check again before insertion
                    erros.add("error.unauthorized.addBugToSession");
                    sessao = null; // Invalidate session object for further processing if unauthorized
                }
            } else {
                // If idSessao itself was invalid or missing, and we already added an error for it.
                if (!erros.isExisteErros()) erros.add("bug.erro.sessaoContextoInvalido");
            }


            if (erros.isExisteErros() || sessao == null) {
                Bug bugComInputUsuario = new Bug();
                if (idSessao != null) bugComInputUsuario.setIdSessao(idSessao);
                bugComInputUsuario.setDescricao(descricao);
                request.setAttribute("bug", bugComInputUsuario);
                if (sessao != null) request.setAttribute("sessao", sessao); // If session object exists
                else if (idSessao != null) request.setAttribute("idSessao", idSessao);
                request.setAttribute("isEditModeBug", false);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/bug/formulario.jsp");
                dispatcher.forward(request, response);
                return;
            }

            Bug novoBug = new Bug(idSessao, descricao, LocalDateTime.now());
            // 'resolvido' defaults to false in this constructor
            bugDAO.insert(novoBug);
            response.sendRedirect(request.getContextPath() + "/bugs/lista-sessao?idSessao=" + idSessao);

        } catch (Exception e) {
            erros.add("bug.erro.inesperadoInsercao");
            e.printStackTrace();
            Bug bugComInputUsuario = new Bug();
            if (idSessao != null) bugComInputUsuario.setIdSessao(idSessao);
            bugComInputUsuario.setDescricao(descricao);
            request.setAttribute("bug", bugComInputUsuario);
            if (idSessao != null) {
                SessaoTeste s = sessaoTesteDAO.getById(idSessao);
                if (s != null) request.setAttribute("sessao", s);
                else request.setAttribute("idSessao", idSessao);
            }
            request.setAttribute("isEditModeBug", false);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/bug/formulario.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void apresentaFormEdicaoBug(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idBugParam = request.getParameter("idBug");
        Long idBug;

        if (idBugParam == null || idBugParam.isEmpty()) {
            erros.add("bug.erro.idBugObrigatorio");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            idBug = Long.parseLong(idBugParam);
        } catch (NumberFormatException e) {
            erros.add("bug.erro.idBugInvalido");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        Bug bug = bugDAO.getById(idBug);
        if (bug == null) {
            erros.add("bug.erro.bugNaoEncontrado");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        SessaoTeste sessaoDoBug = sessaoTesteDAO.getById(bug.getIdSessao());
        if (sessaoDoBug == null) {
            erros.add("sessao.erro.sessaoDoBugNaoEncontrada"); // Should not happen if DB is consistent
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Authorization: Only tester of the session or admin can edit bugs
        if (!isAdmin(usuarioLogado) && !(isTester(usuarioLogado) && usuarioLogado.getId().equals(sessaoDoBug.getIdTester()))) {
            acessoNegado(request, response, erros, "error.unauthorized.editBug");
            return;
        }

        request.setAttribute("bug", bug);
        request.setAttribute("sessao", sessaoDoBug); // Pass session for context
        request.setAttribute("idSessao", sessaoDoBug.getId());
        request.setAttribute("isEditModeBug", true);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/bug/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void atualizaBug(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String idBugParam = request.getParameter("idBug");
        String descricao = request.getParameter("descricao");
        boolean resolvido = "on".equalsIgnoreCase(request.getParameter("resolvido")) || "true".equalsIgnoreCase(request.getParameter("resolvido"));
        Long idBug = null;

        try {
            if (idBugParam == null || idBugParam.isEmpty()) {
                erros.add("bug.erro.idBugObrigatorio");
            } else {
                try {
                    idBug = Long.parseLong(idBugParam);
                } catch (NumberFormatException e) {
                    erros.add("bug.erro.idBugInvalido");
                }
            }

            if (descricao == null || descricao.trim().isEmpty()) {
                erros.add("bug.erro.descricaoObrigatoria");
            }

            Bug bugParaAtualizar = null;
            SessaoTeste sessaoDoBug = null;

            if (idBug != null) {
                bugParaAtualizar = bugDAO.getById(idBug);
                if (bugParaAtualizar == null) {
                    erros.add("bug.erro.bugNaoEncontrado");
                } else {
                    sessaoDoBug = sessaoTesteDAO.getById(bugParaAtualizar.getIdSessao());
                    if (sessaoDoBug == null) {
                        // This case indicates data inconsistency, should ideally not happen
                        erros.add("sessao.erro.sessaoDoBugNaoEncontrada");
                        bugParaAtualizar = null; // Invalidate bug object for further processing
                    } else if (!isAdmin(usuarioLogado) && !(isTester(usuarioLogado) && usuarioLogado.getId().equals(sessaoDoBug.getIdTester()))) {
                        erros.add("error.unauthorized.editBug");
                        bugParaAtualizar = null; // Invalidate bug object
                    }
                }
            }


            if (erros.isExisteErros() || bugParaAtualizar == null) {
                // Repopulate form with user input
                Bug bugComInputUsuario = new Bug();
                bugComInputUsuario.setId(idBug);
                bugComInputUsuario.setDescricao(descricao);
                bugComInputUsuario.setResolvido(resolvido);
                if (bugParaAtualizar != null) { // If bug existed but had other errors
                    bugComInputUsuario.setIdSessao(bugParaAtualizar.getIdSessao());
                    request.setAttribute("sessao", sessaoDoBug);
                    request.setAttribute("idSessao", bugParaAtualizar.getIdSessao());
                }
                request.setAttribute("bug", bugComInputUsuario);
                request.setAttribute("isEditModeBug", true);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/bug/formulario.jsp");
                dispatcher.forward(request, response);
                return;
            }

            bugParaAtualizar.setDescricao(descricao);
            bugParaAtualizar.setResolvido(resolvido);
            // Data do bug (data de reporte) não deve ser alterada na atualização.
            bugDAO.update(bugParaAtualizar);
            response.sendRedirect(request.getContextPath() + "/bugs/lista-sessao?idSessao=" + bugParaAtualizar.getIdSessao());

        } catch (Exception e) {
            erros.add("bug.erro.inesperadoAtualizacao");
            e.printStackTrace();
            Bug bugComInputUsuario = new Bug();
            bugComInputUsuario.setId(idBug);
            bugComInputUsuario.setDescricao(descricao);
            bugComInputUsuario.setResolvido(resolvido);
            if (idBug != null) { // Try to get original session ID if bug ID was valid
                Bug originalBug = bugDAO.getById(idBug);
                if (originalBug != null) {
                    SessaoTeste s = sessaoTesteDAO.getById(originalBug.getIdSessao());
                    if (s != null) request.setAttribute("sessao", s);
                    request.setAttribute("idSessao", originalBug.getIdSessao());
                }
            }
            request.setAttribute("bug", bugComInputUsuario);
            request.setAttribute("isEditModeBug", true);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/bug/formulario.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void removeBug(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idBugParam = request.getParameter("idBug");
        Long idBug;

        if (idBugParam == null || idBugParam.isEmpty()) {
            erros.add("bug.erro.idBugObrigatorioRemocao");
        } else {
            try {
                idBug = Long.parseLong(idBugParam);
                Bug bug = bugDAO.getById(idBug);

                if (bug == null) {
                    erros.add("bug.erro.bugNaoEncontradoRemocao");
                } else {
                    SessaoTeste sessaoDoBug = sessaoTesteDAO.getById(bug.getIdSessao());
                    if (sessaoDoBug == null) {
                        erros.add("sessao.erro.sessaoDoBugNaoEncontrada"); // Data inconsistency
                    } else if (!isAdmin(usuarioLogado) && !(isTester(usuarioLogado) && usuarioLogado.getId().equals(sessaoDoBug.getIdTester()))) {
                        erros.add("error.unauthorized.deleteBug");
                    } else {
                        // All checks passed, proceed with deletion
                        bugDAO.delete(idBug);
                        response.sendRedirect(request.getContextPath() + "/bugs/lista-sessao?idSessao=" + bug.getIdSessao());
                        return; // Success, exit method
                    }
                }
            } catch (NumberFormatException e) {
                erros.add("bug.erro.idBugInvalido");
            } catch (RuntimeException e) {
                erros.add("bug.erro.inesperadoRemocao");
                e.printStackTrace();
            }
        }

        // If any error occurred or deletion didn't happen and redirect
        request.getSession().setAttribute("mensagensFlash", erros);
        String referer = request.getHeader("Referer");
        // Try to redirect to a sensible page, avoiding redirecting to the delete action itself
        String fallbackUrl = request.getContextPath() + ( (request.getParameter("idSessao") != null) ? "/sessoes/detalhes?idSessao=" + request.getParameter("idSessao") : "/usuario/" );

        if (referer != null && !referer.isEmpty() && !referer.contains("/bugs/remocao")) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(fallbackUrl);
        }
    }

    private void listaBugsPorSessao(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idSessaoParam = request.getParameter("idSessao");
        Long idSessao;

        if (idSessaoParam == null || idSessaoParam.isEmpty()) {
            erros.add("bug.erro.idSessaoObrigatorio"); // Use uma chave de erro apropriada
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        try {
            idSessao = Long.parseLong(idSessaoParam);
        } catch (NumberFormatException e) {
            erros.add("bug.erro.idSessaoInvalido");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        SessaoTeste sessao = sessaoTesteDAO.getById(idSessao); // sessaoTesteDAO injetado no init
        if (sessao == null) {
            erros.add("sessao.erro.sessaoNaoEncontrada");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (!isAdmin(usuarioLogado) && !(isTester(usuarioLogado) && usuarioLogado.getId().equals(sessao.getIdTester()))) {
            acessoNegado(request, response, erros, "error.unauthorized.viewSessionBugs"); // Nova chave de erro
            return;
        }

        List<Bug> listaBugs = bugDAO.getAllBySessaoId(idSessao); // bugDAO injetado no init

        request.setAttribute("sessao", sessao); // Objeto SessaoTeste para contexto
        request.setAttribute("listaBugs", listaBugs);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/bug/listaPorSessao.jsp");
        dispatcher.forward(request, response);
    }
}