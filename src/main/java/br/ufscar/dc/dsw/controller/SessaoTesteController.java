package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.ProjetoDAO;
import br.ufscar.dc.dsw.dao.SessaoTesteDAO;
import br.ufscar.dc.dsw.domain.*;
import br.ufscar.dc.dsw.util.Erro;
import br.ufscar.dc.dsw.util.StatusSessaoTeste;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(urlPatterns = "/sessao/*")
public class SessaoTesteController extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private SessaoTesteDAO sessaoTesteDAO;
    private ProjetoDAO projetoDAO;
    // private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        sessaoTesteDAO = new SessaoTesteDAO();
        projetoDAO = new ProjetoDAO();
        // estrategiaDAO = new EstrategiaDAO();
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

        if (action == null || action.equals("/") || action.isEmpty()) {
            action = "/minhasSessoes";
        }

        Erro flashErros = (Erro) request.getSession().getAttribute("mensagensFlash");
        if (flashErros != null) {
            erros.getErros().addAll(flashErros.getErros());
            request.getSession().removeAttribute("mensagensFlash");
        }
        try {
            System.out.println("teste" + action);
            switch (action) {
                case "/cadastro.jsp":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        apresentaFormCadastro(request, response, erros);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/insercao.jsp":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        insere(request, response, erros, usuarioLogado);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/listaPorProjeto.jsp":
                    listaPorProjeto(request, response, erros, usuarioLogado);
                    break;
                case "/minhasSessoes.jsp":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        listaPorTester(request, response, erros, usuarioLogado);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/detalhes.jsp":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        detalhesSessao(request, response, erros, usuarioLogado);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/atualizaStatus.jsp":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        atualizaStatus(request, response, erros, usuarioLogado);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/remocao.jsp":
                    if (isAdmin(usuarioLogado)) {
                        remove(request, response, erros);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                default:
                    erros.add("sessao.erro.acaoNaoEncontrada");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
                    dispatcher.forward(request, response);
                    break;
            }
        } catch (Exception e) {
            erros.add("Erro interno do sistema: " + e.getMessage());
            e.printStackTrace();
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
        if (!erros.isExisteErros()) {
            erros.add(chaveMensagem);
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/noAuth.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response, Erro erros)
            throws ServletException, IOException {
        String idProjetoParam = request.getParameter("idProjeto");
        if (idProjetoParam == null || idProjetoParam.isEmpty()) {
            erros.add("sessao.erro.idProjetoObrigatorio");

            response.sendRedirect(request.getContextPath() + "/admin/projetos/lista");
            return;
        }
        try {
            Long idProjeto = Long.parseLong(idProjetoParam);
            Projeto projeto = projetoDAO.get(idProjeto);
            if (projeto == null) {
                erros.add("sessao.erro.projetoNaoEncontrado");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
                dispatcher.forward(request, response);
                return;
            }
            // List<Estrategia> estrategias = estrategiaDAO.getAll();
            // request.setAttribute("estrategias", estrategias);

            request.setAttribute("projeto", projeto);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/formulario.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException e) {
            erros.add("sessao.erro.idProjetoInvalido");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void insere(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long idProjeto = null;
        Long idEstrategia = null;
        String idProjetoParam = request.getParameter("idProjeto");
        String idEstrategiaParam = request.getParameter("idEstrategia");
        String duracaoStr = request.getParameter("duracao");
        String descricao = request.getParameter("descricao");

        try {
            if (idProjetoParam == null || idProjetoParam.isEmpty()) {
                erros.add("sessao.erro.idProjetoObrigatorio");
            } else {
                try {
                    idProjeto = Long.parseLong(idProjetoParam);
                } catch (NumberFormatException e) {
                    erros.add("sessao.erro.idProjetoInvalido");
                }
            }

            if (idEstrategiaParam == null || idEstrategiaParam.isEmpty()) {
                erros.add("sessao.erro.idEstrategiaObrigatorio");
            } else {
                try {
                    idEstrategia = Long.parseLong(idEstrategiaParam);
                } catch (NumberFormatException e) {
                    erros.add("sessao.erro.idEstrategiaInvalido");
                }
            }

            if (duracaoStr == null || duracaoStr.trim().isEmpty()) {
                erros.add("sessao.erro.duracaoObrigatoria");
            }
            if (descricao == null || descricao.trim().isEmpty()) {
                erros.add("sessao.erro.descricaoObrigatoria");
            }

            Time duracao = null;
            if (duracaoStr != null && !duracaoStr.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = duracaoStr.length() > 5 ? new SimpleDateFormat("HH:mm:ss") : new SimpleDateFormat("HH:mm");
                    duracao = new Time(sdf.parse(duracaoStr).getTime());
                } catch (ParseException e) {
                    erros.add("sessao.erro.formatoDuracaoInvalido");
                }
            }

            if (idProjeto != null) {
                Projeto projeto = projetoDAO.get(idProjeto);
                if (projeto == null) {
                    erros.add("sessao.erro.projetoNaoEncontrado");
                } else if (!isAdmin(usuarioLogado) && !projetoDAO.isUsuarioMembro(idProjeto, usuarioLogado.getId())) {
                    erros.add("error.unauthorized.title");
                }
                request.setAttribute("projeto", projeto);
            }


            if (erros.isExisteErros()) {
                request.setAttribute("idProjetoParam", idProjetoParam);
                request.setAttribute("idEstrategiaParam", idEstrategiaParam);
                request.setAttribute("duracaoParam", duracaoStr);
                request.setAttribute("descricaoParam", descricao);
                // List<Estrategia> estrategias = estrategiaDAO.getAll();
                // request.setAttribute("estrategias", estrategias);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/formulario.jsp");
                dispatcher.forward(request, response);
                return;
            }

            SessaoTeste sessao = new SessaoTeste(idProjeto, usuarioLogado.getId(), idEstrategia, duracao, descricao);
            sessaoTesteDAO.insert(sessao);
            response.sendRedirect(request.getContextPath() + "/sessao/detalhes?idSessao=" + sessao.getId());

        } catch (Exception e) {
            erros.add("sessao.erro.inesperadoInsercao");
            e.printStackTrace();
            request.setAttribute("idProjetoParam", idProjetoParam);
            request.setAttribute("idEstrategiaParam", idEstrategiaParam);
            request.setAttribute("duracaoParam", duracaoStr);
            request.setAttribute("descricaoParam", descricao);
            if (idProjeto != null) request.setAttribute("projeto", projetoDAO.get(idProjeto));
            // List<Estrategia> estrategias = estrategiaDAO.getAll();
            // request.setAttribute("estrategias", estrategias);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/formulario.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void listaPorProjeto(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idProjetoParam = request.getParameter("idProjeto");
        if (idProjetoParam == null || idProjetoParam.isEmpty()) {
            erros.add("sessao.erro.idProjetoObrigatorio");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }
        try {
            Long idProjeto = Long.parseLong(idProjetoParam);
            Projeto projeto = projetoDAO.get(idProjeto);
            if (projeto == null) {
                erros.add("sessao.erro.projetoNaoEncontrado");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
                dispatcher.forward(request, response);
                return;
            }
            if (!isAdmin(usuarioLogado) && !projetoDAO.isUsuarioMembro(idProjeto, usuarioLogado.getId())) {
                acessoNegado(request, response, erros, "error.unauthorized.title");
                return;
            }

            List<SessaoTeste> listaSessoes = sessaoTesteDAO.getAllByProjetoId(idProjeto);
            request.setAttribute("projeto", projeto);
            request.setAttribute("listaSessoes", listaSessoes);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/listaPorProjeto.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            erros.add("sessao.erro.idProjetoInvalido");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void listaPorTester(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        // List<SessSessaoTeste> listaSessoes = sessaoTesteDAO.getAllByTesterId(usuarioLogado.getId());
        // request.setAttribute("listaSessoes", listaSessoes);
        request.setAttribute("tituloLista", "minhasSessoes.list.title");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/minhasSessoes.jsp");
        dispatcher.forward(request, response);
    }

    private void detalhesSessao(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idSessaoParam = request.getParameter("idSessao");
        if (idSessaoParam == null || idSessaoParam.isEmpty()) {
            erros.add("sessao.erro.idSessaoObrigatorio");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
            return;
        }
        try {
            Long idSessao = Long.parseLong(idSessaoParam);
            SessaoTeste sessao = sessaoTesteDAO.getById(idSessao);

            if (sessao == null) {
                erros.add("sessao.erro.sessaoNaoEncontrada");
            } else {
                if (!isAdmin(usuarioLogado) && !sessao.getIdTester().equals(usuarioLogado.getId())) {
                    erros.add("error.unauthorized.title");
                    sessao = null;
                }
            }

            if (erros.isExisteErros() && sessao == null) {
                acessoNegado(request, response, erros, "error.unauthorized.title");
                return;
            }
            if (erros.isExisteErros()){
                RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
                dispatcher.forward(request, response);
                return;
            }

            request.setAttribute("sessao", sessao);
            request.setAttribute("todosStatusSessao", StatusSessaoTeste.values());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/detalhes.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            erros.add("sessao.erro.idSessaoInvalido");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void atualizaStatus(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idSessaoParam = request.getParameter("idSessao");
        String novoStatusStr = request.getParameter("novoStatus");

        if (idSessaoParam == null || novoStatusStr == null || novoStatusStr.trim().isEmpty()) {
            erros.add("sessao.erro.idSessaoStatusObrigatorio");
            request.getSession().setAttribute("mensagensFlash", erros);
            response.sendRedirect(request.getHeader("Referer") != null ? request.getHeader("Referer") : request.getContextPath() + "/sessao/minhasSessoes");
            return;
        }

        Long idSessao = null;
        try {
            idSessao = Long.parseLong(idSessaoParam);
            SessaoTeste sessao = sessaoTesteDAO.getById(idSessao);

            StatusSessaoTeste novoStatusEnum;
            try {
                novoStatusEnum = StatusSessaoTeste.fromString(novoStatusStr);
            } catch (IllegalArgumentException e) {
                erros.add("sessao.erro.statusInvalido");
                request.getSession().setAttribute("mensagensFlash", erros);
                response.sendRedirect(request.getContextPath() + "/sessao/detalhes?idSessao=" + idSessao);
                return;
            }

            if (sessao == null) {
                erros.add("sessao.erro.sessaoNaoEncontrada");
            } else if (!isAdmin(usuarioLogado) && !sessao.getIdTester().equals(usuarioLogado.getId())) {
                erros.add("error.unauthorized.title");
            } else if (!isValidStatusTransition(sessao.getStatus(), novoStatusEnum)) {
                erros.add("sessao.erro.transicaoStatusInvalida");
                request.setAttribute("statusAtual", sessao.getStatus().getStatus());
                request.setAttribute("statusNovo", novoStatusEnum.getStatus());
            }

            if (erros.isExisteErros()) {
                request.getSession().setAttribute("mensagensFlash", erros);
                if (request.getAttribute("statusAtual") != null) {
                    request.getSession().setAttribute("flashStatusAtual", request.getAttribute("statusAtual"));
                    request.getSession().setAttribute("flashStatusNovo", request.getAttribute("statusNovo"));
                }
                response.sendRedirect(request.getContextPath() + "/sessao/detalhes?idSessao=" + idSessao);
                return;
            }

            sessaoTesteDAO.updateStatus(idSessao, novoStatusEnum);
            response.sendRedirect(request.getContextPath() + "/sessao/detalhes?idSessao=" + idSessao);

        } catch (NumberFormatException e) {
            erros.add("sessao.erro.idSessaoInvalido");
            request.getSession().setAttribute("mensagensFlash", erros);
            response.sendRedirect(request.getHeader("Referer") != null ? request.getHeader("Referer") : request.getContextPath() + "/sessao/minhasSessoes");
        } catch (RuntimeException e) {
            erros.add("sessao.erro.inesperadoAtualizarStatus");
            e.printStackTrace();
            request.getSession().setAttribute("mensagensFlash", erros);
            String redirectPath = (idSessao != null) ? "/sessao/detalhes?idSessao=" + idSessao : "/sessao/minhasSessoes";
            response.sendRedirect(request.getContextPath() + redirectPath);
        }
    }

    private boolean isValidStatusTransition(StatusSessaoTeste atual, StatusSessaoTeste novo) {
        if (atual == null || novo == null) return false;
        if (atual == novo) return true;
        return switch (atual) {
            case CREATED -> novo == StatusSessaoTeste.IN_EXECUTION;
            case IN_EXECUTION -> novo == StatusSessaoTeste.FINALIZED;
            default -> false;
        };
    }

    private void remove(HttpServletRequest request, HttpServletResponse response, Erro erros)
            throws IOException, ServletException {
        String idSessaoParam = request.getParameter("idSessao");
        Long idProjetoRedirect = null;

        if (idSessaoParam == null || idSessaoParam.isEmpty()) {
            erros.add("sessao.erro.idSessaoObrigatorioRemocao");
        } else {
            try {
                Long idSessao = Long.parseLong(idSessaoParam);
                SessaoTeste sessao = sessaoTesteDAO.getById(idSessao);
                if (sessao != null) {
                    idProjetoRedirect = sessao.getIdProjeto();
                    sessaoTesteDAO.delete(idSessao);
                } else {
                    erros.add("sessao.erro.sessaoNaoEncontradaRemocao");
                }
            } catch (NumberFormatException e) {
                erros.add("sessao.erro.idSessaoInvalido");
            } catch (RuntimeException e) {
                erros.add("sessao.erro.inesperadoRemocao");
                e.printStackTrace();
            }
        }

        String redirectUrl;
        if (erros.isExisteErros()) {
            request.getSession().setAttribute("mensagensFlash", erros);
            redirectUrl = (idProjetoRedirect != null) ?
                    request.getContextPath() + "/sessao/listaPorProjeto?idProjeto=" + idProjetoRedirect :
                    (idSessaoParam != null && !idSessaoParam.isEmpty() ? request.getContextPath() + "/sessao/detalhes?idSessao=" + idSessaoParam : request.getContextPath() + "/usuario/");
            response.sendRedirect(redirectUrl);
            return;
        }
        redirectUrl = (idProjetoRedirect != null) ?
                request.getContextPath() + "/sessao/listaPorProjeto?idProjeto=" + idProjetoRedirect :
                request.getContextPath() + "/usuario/";
        response.sendRedirect(redirectUrl);
    }
}