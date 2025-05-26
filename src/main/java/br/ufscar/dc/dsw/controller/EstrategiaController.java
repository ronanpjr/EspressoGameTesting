package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.EstrategiaDAO;
import br.ufscar.dc.dsw.domain.DicaEstrategia;
import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Exemplo;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@WebServlet("/estrategia")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 15   // 15 MB
)
public class EstrategiaController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    private final String UPLOAD_SUB_DIRECTORY_FOR_FILESYSTEM = "uploads" + File.separator + "estrategias";
    private final String UPLOAD_SUB_DIRECTORY_FOR_URL = "uploads/estrategias";
    public static final String APP_SCOPE_UPLOAD_URL_PATH = "uploadUrlPath";


    @Override
    public void init() throws ServletException {
        super.init();
        estrategiaDAO = new EstrategiaDAO();

        ServletContext context = getServletContext();
        context.setAttribute(APP_SCOPE_UPLOAD_URL_PATH, UPLOAD_SUB_DIRECTORY_FOR_URL);
        System.out.println("INFO: Atributo '" + APP_SCOPE_UPLOAD_URL_PATH + "' definido no escopo da aplicação como: " + UPLOAD_SUB_DIRECTORY_FOR_URL);

        String fullUploadPath = getUploadDirectoryPath();
        File uploadDir = new File(fullUploadPath);
        if (!uploadDir.exists()) {
            if (uploadDir.mkdirs()) {
                System.out.println("INFO: Diretório de upload criado em: " + fullUploadPath);
            } else {
                System.err.println("AVISO: Falha ao criar diretório de upload: " + fullUploadPath + ". Verifique as permissões.");
            }
        }
    }

    private String getUploadDirectoryPath() {
        String appPath = getServletContext().getRealPath("");
        return appPath + File.separator + UPLOAD_SUB_DIRECTORY_FOR_FILESYSTEM;
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String cd : contentDisposition.split(";")) {
                if (cd.trim().startsWith("filename")) {
                    String fileName = cd.substring(cd.indexOf('=') + 1).trim();
                    return fileName.replace("\"", "");
                }
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html; charset=UTF-8");

        String acao = request.getParameter("acao");
        if (acao == null || acao.isEmpty()) {
            acao = "listar";
        }

        try {
            switch (acao) {
                case "novo":
                    mostrarFormularioNovo(request, response);
                    break;
                case "editar":
                    mostrarFormularioEditar(request, response);
                    break;
                case "excluir":
                    excluirEstrategia(request, response);
                    break;
                case "detalhes":
                    verDetalhesEstrategia(request, response);
                    break;
                case "listar":
                default:
                    listarEstrategias(request, response);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Erro no doGet EstrategiaController: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("erroMsgGeral", "Ocorreu um erro inesperado: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/erro.jsp");
            dispatcher.forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html; charset=UTF-8");

        String acao = request.getParameter("acao");
        if (acao == null) {
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar");
            return;
        }

        try {
            if ("salvar".equals(acao)) {
                salvarEstrategia(request, response);
            } else {
                listarEstrategias(request, response);
            }
        } catch (Exception e) {
            System.err.println("Erro no doPost EstrategiaController: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("erroMsg", "Erro ao processar requisição: " + e.getMessage());

            Estrategia estrategiaComErro = new Estrategia();
            try {
                estrategiaComErro = popularEstrategiaComRequest(request, true);
            } catch (Exception exPopulate) {
                System.err.println("Erro ao tentar repopular formulário após erro: " + exPopulate.getMessage());
                String idParam = request.getParameter("id_estrategia");
                if (idParam != null && !idParam.isEmpty()) {
                    try { estrategiaComErro.setId(Integer.parseInt(idParam)); } catch (NumberFormatException nfe) {}
                }
                estrategiaComErro.setNome(request.getParameter("nome"));
                estrategiaComErro.setDescricao(request.getParameter("descricao"));
            }
            request.setAttribute("estrategia", estrategiaComErro);
            request.setAttribute("acaoForm", "salvar");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/estrategia/formulario.jsp");
            dispatcher.forward(request, response);
        }
    }

    private Estrategia popularEstrategiaComRequest(HttpServletRequest request, boolean isErrorReporting) throws IOException, ServletException {
        Estrategia estrategia = new Estrategia();
        String idParam = request.getParameter("id_estrategia");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                estrategia.setId(Integer.parseInt(idParam));
            } catch (NumberFormatException e) {}
        }
        estrategia.setNome(request.getParameter("nome"));
        estrategia.setDescricao(request.getParameter("descricao"));

        String[] textosExemplo = request.getParameterValues("textoExemplo[]");
        String[] atributos1Exemplo = request.getParameterValues("atributo1Exemplo[]");
        String[] urlsImagemExemploAtual = request.getParameterValues("urlImagemExemploAtual[]");

        List<Part> imagemParts = null;
        if (request.getContentType() != null && request.getContentType().toLowerCase().contains("multipart/form-data")) {
            try {
                imagemParts = request.getParts().stream()
                        .filter(part -> "imagemExemplo[]".equals(part.getName()))
                        .collect(Collectors.toList());
            } catch (IOException | ServletException e) {
                System.err.println("Erro ao obter partes do formulário multipart: " + e.getMessage());
                if (!isErrorReporting) throw e;
            }
        }


        if (textosExemplo != null) {
            for (int i = 0; i < textosExemplo.length; i++) {
                String texto = textosExemplo[i];
                if (texto != null && !texto.trim().isEmpty()) {
                    Exemplo exemplo = new Exemplo();
                    exemplo.setTexto(texto);

                    String nomeArquivoParaSalvarNoBanco = null;
                    Part filePart = null;

                    if (imagemParts != null && i < imagemParts.size()) {
                        filePart = imagemParts.get(i);
                    }

                    if (filePart != null && filePart.getSize() > 0) {
                        String originalFileName = getFileName(filePart);
                        if (originalFileName != null && !originalFileName.isEmpty()) {
                            String fileExtension = "";
                            int dotIndex = originalFileName.lastIndexOf('.');
                            if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                                fileExtension = originalFileName.substring(dotIndex);
                            }
                            nomeArquivoParaSalvarNoBanco = UUID.randomUUID().toString() + fileExtension;

                            if (!isErrorReporting) {
                                String diretorioDeUploadFisico = getUploadDirectoryPath();
                                File fileNoDisco = new File(diretorioDeUploadFisico, nomeArquivoParaSalvarNoBanco);

                                try (InputStream fileContent = filePart.getInputStream()) {
                                    Files.copy(fileContent, fileNoDisco.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    System.out.println("INFO: Arquivo de upload salvo em: " + fileNoDisco.getAbsolutePath());
                                } catch (IOException e) {
                                    System.err.println("ERRO ao salvar arquivo de upload: " + nomeArquivoParaSalvarNoBanco + ". Detalhes: " + e.getMessage());
                                    e.printStackTrace();
                                    if (urlsImagemExemploAtual != null && i < urlsImagemExemploAtual.length &&
                                            urlsImagemExemploAtual[i] != null && !urlsImagemExemploAtual[i].isEmpty()) {
                                        nomeArquivoParaSalvarNoBanco = urlsImagemExemploAtual[i];
                                    } else {
                                        nomeArquivoParaSalvarNoBanco = null;
                                    }
                                }
                            }
                        }
                    }

                    if (nomeArquivoParaSalvarNoBanco == null &&
                            urlsImagemExemploAtual != null && i < urlsImagemExemploAtual.length &&
                            urlsImagemExemploAtual[i] != null && !urlsImagemExemploAtual[i].isEmpty()) {
                        nomeArquivoParaSalvarNoBanco = urlsImagemExemploAtual[i];
                    }
                    exemplo.setUrlImagem(nomeArquivoParaSalvarNoBanco);
                    estrategia.addExemplo(exemplo);
                }
            }
        }

        // Coletar Dicas
        String[] dicasTexto = request.getParameterValues("dicaEstrategia[]");
        if (dicasTexto != null) {
            for (String textoDica : dicasTexto) {
                if (textoDica != null && !textoDica.trim().isEmpty()) {
                    DicaEstrategia dica = new DicaEstrategia();
                    dica.setDica(textoDica);
                    estrategia.addDica(dica);
                }
            }
        }
        return estrategia;
    }

    private void salvarEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Estrategia estrategia = popularEstrategiaComRequest(request, false);
        try {
            estrategiaDAO.salvar(estrategia);
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&sucesso=salvo");
        } catch (RuntimeException e) {
            System.err.println("Erro ao tentar salvar estratégia no DAO: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("erroMsg", "Erro ao salvar estratégia: " + e.getMessage());
            Estrategia estrategiaParaFormulario = popularEstrategiaComRequest(request, true);
            request.setAttribute("estrategia", estrategiaParaFormulario);
            request.setAttribute("acaoForm", "salvar");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/estrategia/formulario.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void listarEstrategias(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Estrategia> listaEstrategias = estrategiaDAO.listarTodas();
        request.setAttribute("listaEstrategias", listaEstrategias);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/estrategia/listar.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormularioNovo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("estrategia", new Estrategia());
        request.setAttribute("acaoForm", "salvar");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/estrategia/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&erro=id_invalido");
            return;
        }

        Estrategia estrategiaExistente = estrategiaDAO.buscarPorId(id);
        if (estrategiaExistente == null) {
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&erro=nao_encontrado");
            return;
        }
        request.setAttribute("estrategia", estrategiaExistente);
        request.setAttribute("acaoForm", "salvar");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/estrategia/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void excluirEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&erro=id_invalido_exclusao");
            return;
        }

        Estrategia estrategiaParaExcluir = estrategiaDAO.buscarPorId(id);
        if (estrategiaParaExcluir != null && estrategiaParaExcluir.getExemplos() != null) {
            String diretorioDeUploadFisico = getUploadDirectoryPath();
            for (Exemplo ex : estrategiaParaExcluir.getExemplos()) {
                if (ex.getUrlImagem() != null && !ex.getUrlImagem().isEmpty()) {
                    File imagemParaExcluirDoDisco = new File(diretorioDeUploadFisico, ex.getUrlImagem());
                    if (imagemParaExcluirDoDisco.exists()) {
                        if (imagemParaExcluirDoDisco.delete()) {
                            System.out.println("INFO: Imagem excluída do disco: " + imagemParaExcluirDoDisco.getAbsolutePath());
                        } else {
                            System.err.println("ERRO: Falha ao excluir imagem do disco: " + imagemParaExcluirDoDisco.getAbsolutePath());
                        }
                    }
                }
            }
        }

        try {
            estrategiaDAO.excluir(id);
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&sucesso=excluido");
        } catch (RuntimeException e) {
            System.err.println("Erro ao excluir estratégia do DAO: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&erro=exclusao_falhou&msg=" + java.net.URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.name()));
        }
    }

    private void verDetalhesEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&erro=id_invalido_detalhes");
            return;
        }
        Estrategia estrategia = estrategiaDAO.buscarPorId(id);
        if (estrategia == null) {
            response.sendRedirect(request.getContextPath() + "/estrategia?acao=listar&erro=nao_encontrado_detalhes");
            return;
        }
        request.setAttribute("estrategia", estrategia);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/estrategia/detalhes.jsp");
        dispatcher.forward(request, response);
    }
}