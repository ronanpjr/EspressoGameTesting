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

@WebServlet(urlPatterns = "/sessoes/*")
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
            action = "/minhas-sessoes";
        }
        Erro flashErros = (Erro) request.getSession().getAttribute("mensagensFlash");
        if (flashErros != null) {
            erros.getErros().addAll(flashErros.getErros());
            request.getSession().removeAttribute("mensagensFlash");
        }
        try {
            switch (action) {
                case "/cadastro":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        apresentaFormCadastro(request, response, erros);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/insercao":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        insere(request, response, erros, usuarioLogado);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/edicao":
                    apresentaFormEdicao(request, response, erros, usuarioLogado);
                    break;
                case "/atualizacao":
                    updateSessao(request, response, erros, usuarioLogado);
                    break;
                case "/lista-projeto":
                    listaPorProjeto(request, response, erros, usuarioLogado);
                    break;
                case "/minhas-sessoes":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        listaPorTester(request, response, erros, usuarioLogado);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/detalhes":
                    detalhesSessao(request, response, erros, usuarioLogado);
                    break;
                case "/atualizaStatus":
                    if (isTester(usuarioLogado) || isAdmin(usuarioLogado)) {
                        atualizaStatus(request, response, erros, usuarioLogado);
                    } else {
                        acessoNegado(request, response, erros, "error.unauthorized.title");
                    }
                    break;
                case "/remocao":
                    remove(request, response, erros, usuarioLogado);
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

    private void apresentaFormEdicao(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idSessaoParam = request.getParameter("idSessao");
        if (idSessaoParam == null || idSessaoParam.isEmpty()) {
            erros.add("sessao.erro.idSessaoObrigatorio");
            response.sendRedirect(request.getContextPath() + "/sessoes/minhas-sessoes");
            return;
        }
        try {
            Long idSessao = Long.parseLong(idSessaoParam);
            SessaoTeste sessao = sessaoTesteDAO.getById(idSessao);
            if (sessao == null) {
                erros.add("sessao.erro.sessaoNaoEncontrada");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
                dispatcher.forward(request, response);
                return;
            }
            if (!isAdmin(usuarioLogado) && !sessao.getIdTester().equals(usuarioLogado.getId())) {
                acessoNegado(request, response, erros, "error.unauthorized.title");
                return;
            }
            request.setAttribute("sessao", sessao);
            request.setAttribute("todosStatusSessao", StatusSessaoTeste.values());
            request.setAttribute("isEditMode", true);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/formulario.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException e) {
            erros.add("sessao.erro.idSessaoInvalido");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void updateSessao(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String idSessaoParam = request.getParameter("idSessao");
        String idProjetoParam = request.getParameter("idProjeto");
        String duracaoStr = request.getParameter("duracao");
        String descricao = request.getParameter("descricao");
        String novoStatusStr = request.getParameter("status");
        Long idSessao = null;
        Long idProjeto = null;
        try {
            if (idSessaoParam == null || idSessaoParam.isEmpty()) {
                erros.add("sessao.erro.idSessaoObrigatorio");
            } else {
                try {
                    idSessao = Long.parseLong(idSessaoParam);
                } catch (NumberFormatException e) {
                    erros.add("sessao.erro.idSessaoInvalido");
                }
            }
            if (idProjetoParam != null && !idProjetoParam.isEmpty()) {
                idProjeto = Long.parseLong(idProjetoParam);
            }
            if (duracaoStr == null || duracaoStr.trim().isEmpty()) {
                erros.add("sessao.erro.duracaoObrigatoria");
            }
            if (descricao == null || descricao.trim().isEmpty()) {
                erros.add("sessao.erro.descricaoObrigatoria");
            }
            if (novoStatusStr == null || novoStatusStr.trim().isEmpty()) {
                erros.add("sessao.erro.statusObrigatorio");
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

            StatusSessaoTeste novoStatusEnum = null;
            if (novoStatusStr != null && !novoStatusStr.trim().isEmpty()) {
                try {
                    novoStatusEnum = StatusSessaoTeste.fromString(novoStatusStr);
                } catch (IllegalArgumentException e) {
                    erros.add("sessao.erro.statusInvalido");
                }
            }

            SessaoTeste sessaoParaAtualizar = null;
            if (idSessao != null) {
                sessaoParaAtualizar = sessaoTesteDAO.getById(idSessao);
                if (sessaoParaAtualizar == null) {
                    erros.add("sessao.erro.sessaoNaoEncontrada");
                } else {
                    if (!isAdmin(usuarioLogado) && !sessaoParaAtualizar.getIdTester().equals(usuarioLogado.getId())) {
                        erros.add("error.unauthorized.title");
                        sessaoParaAtualizar = null;
                    } else if (novoStatusEnum != null && sessaoParaAtualizar.getStatus() != novoStatusEnum && !isValidStatusTransition(sessaoParaAtualizar.getStatus(), novoStatusEnum)) {
                        erros.add("sessao.erro.transicaoStatusInvalida");
                        request.setAttribute("statusAtual", sessaoParaAtualizar.getStatus().name());
                        request.setAttribute("statusNovo", novoStatusEnum.name());
                    }
                }
            }

            if (erros.isExisteErros() || sessaoParaAtualizar == null) {
                SessaoTeste sessaoComInputUsuario = new SessaoTeste();
                sessaoComInputUsuario.setId(idSessao);
                sessaoComInputUsuario.setDescricao(descricao);
                if(sessaoParaAtualizar != null) {
                    sessaoComInputUsuario.setProjeto(sessaoParaAtualizar.getProjeto());
                    sessaoComInputUsuario.setIdProjeto(sessaoParaAtualizar.getIdProjeto());
                    //  sessaoComInputUsuario.setIdEstrategia(sessaoParaAtualizar.getIdEstrategia());
                    //   sessaoComInputUsuario.setEstrategia(sessaoParaAtualizar.getEstrategia());
                } else if (idProjeto != null) {
                    Projeto p = projetoDAO.get(idProjeto);
                    sessaoComInputUsuario.setProjeto(p);
                    sessaoComInputUsuario.setIdProjeto(idProjeto);
                }
                request.setAttribute("sessao", sessaoComInputUsuario);
                request.setAttribute("duracaoParam", duracaoStr);
                request.setAttribute("descricaoParam", descricao);
                request.setAttribute("statusParam", novoStatusStr);
                request.setAttribute("todosStatusSessao", StatusSessaoTeste.values());
                request.setAttribute("isEditMode", true);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/formulario.jsp");
                dispatcher.forward(request, response);
                return;
            }
            sessaoParaAtualizar.setDuracao(duracao);
            sessaoParaAtualizar.setDescricao(descricao);
            sessaoTesteDAO.updateSessao(sessaoParaAtualizar, novoStatusEnum);
            response.sendRedirect(request.getContextPath() + "/sessoes/detalhes?idSessao=" + idSessao);

        } catch (Exception e) {
            erros.add("sessao.erro.inesperadoAtualizacao");
            e.printStackTrace();

            SessaoTeste sessaoFallback = new SessaoTeste();
            sessaoFallback.setId(idSessao);
            if (idProjeto != null) {
                Projeto p = projetoDAO.get(idProjeto);
                sessaoFallback.setProjeto(p);
                sessaoFallback.setIdProjeto(idProjeto);
            }

            request.setAttribute("sessao", sessaoFallback);
            request.setAttribute("duracaoParam", duracaoStr);
            request.setAttribute("descricaoParam", descricao);
            request.setAttribute("statusParam", novoStatusStr);
            request.setAttribute("todosStatusSessao", StatusSessaoTeste.values());
            request.setAttribute("isEditMode", true);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/formulario.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void insere(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long idProjeto = null;
        // Long idEstrategia = null;
        String idProjetoParam = request.getParameter("idProjeto");
        // String idEstrategiaParam = request.getParameter("idEstrategia");
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

//            if (idEstrategiaParam == null || idEstrategiaParam.isEmpty()) {
//                erros.add("sessao.erro.idEstrategiaObrigatorio");
//            } else {
//                try {
//                    idEstrategia = Long.parseLong(idEstrategiaParam);
//                } catch (NumberFormatException e) {
//                    erros.add("sessao.erro.idEstrategiaInvalido");
//                }
//            }

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
                } else if (!isAdmin(usuarioLogado) && !isTester(usuarioLogado)) {
                    erros.add("error.unauthorized.title");
                }
                request.setAttribute("projeto", projeto);
            }


            if (erros.isExisteErros()) {
                request.setAttribute("idProjetoParam", idProjetoParam);
                // request.setAttribute("idEstrategiaParam", idEstrategiaParam);
                request.setAttribute("duracaoParam", duracaoStr);
                request.setAttribute("descricaoParam", descricao);
                // List<Estrategia> estrategias = estrategiaDAO.getAll();
                // request.setAttribute("estrategias", estrategias);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/sessao/formulario.jsp");
                dispatcher.forward(request, response);
                return;
            }

            SessaoTeste sessao = new SessaoTeste(idProjeto, usuarioLogado.getId(), 1L, duracao, descricao);
            sessaoTesteDAO.insert(sessao);
            response.sendRedirect(request.getContextPath() + "/sessoes/detalhes?idSessao=" + sessao.getId());

        } catch (Exception e) {
            erros.add("sessao.erro.inesperadoInsercao");
            e.printStackTrace();
            request.setAttribute("idProjetoParam", idProjetoParam);
            // request.setAttribute("idEstrategiaParam", idEstrategiaParam);
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
            if (!isAdmin(usuarioLogado) && !isTester(usuarioLogado)) {
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
        List<SessaoTeste> listaSessoes = sessaoTesteDAO.getAllByTesterId(usuarioLogado.getId());
        request.setAttribute("listaSessoes", listaSessoes);
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
                    erros.add("Acesso não autorizado!");
                    sessao = null;
                }
            }

            if (erros.isExisteErros() && sessao == null) {
                acessoNegado(request, response, erros, "error.unauthorized.title");
                return;
            }
            if (erros.isExisteErros()) {
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
            response.sendRedirect(request.getHeader("Referer") != null ? request.getHeader("Referer") : request.getContextPath() + "/sessoes/minhasSessoes");
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
                response.sendRedirect(request.getContextPath() + "/sessoes/detalhes?idSessao=" + idSessao);
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
                response.sendRedirect(request.getContextPath() + "/sessoes/detalhes?idSessao=" + idSessao);
                return;
            }

            sessaoTesteDAO.updateStatus(idSessao, novoStatusEnum);
            response.sendRedirect(request.getContextPath() + "/sessoes/detalhes?idSessao=" + idSessao);

        } catch (NumberFormatException e) {
            erros.add("sessao.erro.idSessaoInvalido");
            request.getSession().setAttribute("mensagensFlash", erros);
            response.sendRedirect(request.getHeader("Referer") != null ? request.getHeader("Referer") : request.getContextPath() + "/sessoes/minhasSessoes");
        } catch (RuntimeException e) {
            erros.add("sessao.erro.inesperadoAtualizarStatus");
            e.printStackTrace();
            request.getSession().setAttribute("mensagensFlash", erros);
            String redirectPath = (idSessao != null) ? "/sessoes/detalhes?idSessao=" + idSessao : "/sessoes/minhasSessoes";
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

    private void remove(HttpServletRequest request, HttpServletResponse response, Erro erros, Usuario usuarioLogado)
            throws IOException, ServletException {
        String idSessaoParam = request.getParameter("idSessao");
        Long idSessao = null;
        SessaoTeste sessao = null;
        Long idProjetoDaSessaoRemovida = null;
        if (idSessaoParam == null || idSessaoParam.isEmpty()) {
            erros.add("sessao.erro.idSessaoObrigatorioRemocao");
            request.getSession().setAttribute("mensagensFlash", erros);
            String fallbackUrl = request.getHeader("Referer");
            if (fallbackUrl == null || fallbackUrl.isEmpty() || fallbackUrl.contains("/remocao")) {
                fallbackUrl = request.getContextPath() + "/usuario/";
            }
            response.sendRedirect(fallbackUrl);
            return;
        }
        try {
            idSessao = Long.parseLong(idSessaoParam);
        } catch (NumberFormatException e) {
            erros.add("sessao.erro.idSessaoInvalido");
            request.getSession().setAttribute("mensagensFlash", erros);
            String fallbackUrl = request.getHeader("Referer");
            if (fallbackUrl == null || fallbackUrl.isEmpty() || fallbackUrl.contains("/remocao")) {
                fallbackUrl = request.getContextPath() + "/usuario/";
            }
            response.sendRedirect(fallbackUrl);
            return;
        }
        sessao = sessaoTesteDAO.getById(idSessao);
        if (sessao == null) {
            erros.add("sessao.erro.sessaoNaoEncontradaRemocao");
            request.getSession().setAttribute("mensagensFlash", erros);
            String fallbackUrl = request.getHeader("Referer");
            if (fallbackUrl == null || fallbackUrl.isEmpty() || fallbackUrl.contains("/remocao")) {
                fallbackUrl = request.getContextPath() + "/usuario/";
            }
            response.sendRedirect(fallbackUrl);
            return;
        }
        idProjetoDaSessaoRemovida = sessao.getIdProjeto();
        boolean isAdmin = isAdmin(usuarioLogado);
        boolean isOwnerTester = isTester(usuarioLogado) && sessao.getIdTester().equals(usuarioLogado.getId());
        if (!isAdmin && !isOwnerTester) {
            acessoNegado(request, response, erros, "error.unauthorized.title");
            return;
        }
        try {
            sessaoTesteDAO.delete(idSessao);
            response.sendRedirect(request.getContextPath() + "/sessoes/lista-projeto?idProjeto=" + idProjetoDaSessaoRemovida);
        } catch (RuntimeException e) {
            erros.add("sessao.erro.inesperadoRemocao");
            e.printStackTrace();
            request.getSession().setAttribute("mensagensFlash", erros);
            response.sendRedirect(request.getContextPath() + "/sessoes/lista-projeto?idProjeto=" + idProjetoDaSessaoRemovida);
        }
    }
}