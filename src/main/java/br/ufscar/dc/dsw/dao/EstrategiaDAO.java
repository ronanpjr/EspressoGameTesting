package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.DicaEstrategia;
import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Exemplo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstrategiaDAO extends GenericDAO {

    private ExemploDAO exemploDAO;
    private DicaEstrategiaDAO dicaEstrategiaDAO;

    public EstrategiaDAO() {
        super();
        this.exemploDAO = new ExemploDAO();
        this.dicaEstrategiaDAO = new DicaEstrategiaDAO();
    }

    public void salvar(Estrategia estrategia) {
        String sqlEstrategia;
        boolean isUpdate = estrategia.getId() > 0;

        if (isUpdate) {
            sqlEstrategia = "UPDATE estrategias SET nome = ?, descricao = ? WHERE id_estrategia = ?";
        } else {
            sqlEstrategia = "INSERT INTO estrategias (nome, descricao) VALUES (?, ?)";
        }

        Connection conn = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEstrategia = conn.prepareStatement(sqlEstrategia, Statement.RETURN_GENERATED_KEYS)) {
                stmtEstrategia.setString(1, estrategia.getNome());
                stmtEstrategia.setString(2, estrategia.getDescricao());
                if (isUpdate) {
                    stmtEstrategia.setInt(3, estrategia.getId());
                }
                stmtEstrategia.executeUpdate();

                if (!isUpdate) {
                    try (ResultSet rs = stmtEstrategia.getGeneratedKeys()) {
                        if (rs.next()) {
                            estrategia.setId(rs.getInt(1));
                        }
                    }
                }
            }

            if (isUpdate) {
                exemploDAO.excluirPorEstrategiaId(estrategia.getId(), conn);
                dicaEstrategiaDAO.excluirPorEstrategiaId(estrategia.getId(), conn);
            }

            for (Exemplo exemplo : estrategia.getExemplos()) {
                if (exemplo.getTexto() != null && !exemplo.getTexto().trim().isEmpty()) {
                    exemplo.setIdEstrategia(estrategia.getId());
                    exemploDAO.salvar(exemplo, conn);
                }
            }

            for (DicaEstrategia dica : estrategia.getDicas()) {
                if (dica.getDica() != null && !dica.getDica().trim().isEmpty()) {
                    dica.setIdEstrategia(estrategia.getId());
                    dicaEstrategiaDAO.salvar(dica, conn);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erro ao salvar estratégia: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar estratégia: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    public List<Estrategia> listarTodas() {
        List<Estrategia> estrategias = new ArrayList<>();
        String sql = "SELECT id_estrategia, nome, descricao FROM estrategias ORDER BY nome";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Executando query: " + sql);

            while (rs.next()) {
                Estrategia estrategia = new Estrategia();
                estrategia.setId(rs.getInt("id_estrategia"));
                estrategia.setNome(rs.getString("nome"));
                estrategia.setDescricao(rs.getString("descricao"));
                estrategias.add(estrategia);

                System.out.println("Estratégia encontrada: ID=" + estrategia.getId()
                        + ", Nome=" + estrategia.getNome());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar estratégias: " + e.getMessage());
            e.printStackTrace();
        }
        return estrategias;
    }

    public Estrategia buscarPorId(int id) {
        Estrategia estrategia = null;
        String sql = "SELECT id_estrategia, nome, descricao FROM estrategias WHERE id_estrategia = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estrategia = new Estrategia();
                    estrategia.setId(rs.getInt("id_estrategia"));
                    estrategia.setNome(rs.getString("nome"));
                    estrategia.setDescricao(rs.getString("descricao"));
                    estrategia.setExemplos(exemploDAO.listarPorEstrategiaId(id));
                    estrategia.setDicas(dicaEstrategiaDAO.listarPorEstrategiaId(id));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar estratégia por ID: " + e.getMessage());
        }
        return estrategia;
    }

    public void excluir(int id) {
        String sql = "DELETE FROM estrategias WHERE id_estrategia = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir estratégia: " + e.getMessage());
            throw new RuntimeException("Erro ao excluir estratégia", e);
        }
    }

    public List<Estrategia> getAll() {
        List<Estrategia> estrategias = new ArrayList<>();
        String sql = "SELECT id_estrategia, nome, descricao FROM estrategias ORDER BY nome";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Estrategia estrategia = new Estrategia();
                estrategia.setId(rs.getInt("id_estrategia"));
                estrategia.setNome(rs.getString("nome"));
                estrategia.setDescricao(rs.getString("descricao"));
                // Importante: Não carregamos Exemplos/Dicas aqui para otimizar.
                // Eles são carregados apenas quando se busca por ID.
                estrategias.add(estrategia);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as estratégias (getAll): " + e.getMessage());
            throw new RuntimeException("Erro ao buscar todas as estratégias: " + e.getMessage(), e);
        }
        return estrategias;
    }
}