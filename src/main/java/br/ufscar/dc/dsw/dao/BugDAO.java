package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.Bug;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BugDAO extends GenericDAO {
    public void insert(Bug bug) {
        String sql = "insert into Bug (id_sessao, descricao, data, resolvido) values (?, ?, ?, ?)";
        Connection conexao = null;
        try {
            conexao = this.getConnection();
            conexao.setAutoCommit(false);
            try (PreparedStatement statement = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, bug.getIdSessao());
                statement.setString(2, bug.getDescricao());
                if (bug.getData() != null) {
                    statement.setTimestamp(3, Timestamp.valueOf(bug.getData()));
                } else {
                    statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                }
                statement.setBoolean(4, bug.isResolvido());
                statement.executeUpdate();

                ResultSet result = statement.getGeneratedKeys();
                if (result.next()) {
                    bug.setId(result.getLong(1));
                } else {
                    throw new SQLException("Falha ao obter o ID gerado para o bug");
                }
            }
            conexao.commit();
        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao fazer rollback da inserção do bug: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Erro ao inserir bug: " + e.getMessage(), e);
        } finally {
            if (conexao != null) {
                try {
                    conexao.setAutoCommit(true);
                    conexao.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private Bug mapResultSetToBug(ResultSet rs) throws SQLException {
        Bug bug = new Bug();
        bug.setId(rs.getLong("id_bug"));
        bug.setIdSessao(rs.getLong("id_sessao"));
        bug.setDescricao(rs.getString("descricao"));
        Timestamp dataTimestamp = rs.getTimestamp("data");
        if (dataTimestamp != null) {
            bug.setData(dataTimestamp.toLocalDateTime());
        }
        bug.setResolvido(rs.getBoolean("resolvido"));
        return bug;
    }

    public Bug getById(Long id) {
        Bug bug = null;
        String sql = "select id_bug, id_sessao, descricao, data, resolvido from Bug where id_bug = ?";
        try (Connection conexao = this.getConnection();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                bug = mapResultSetToBug(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar bug por ID: " + e.getMessage(), e);
        }
        return bug;
    }

    public List<Bug> getAllBySessaoId(Long idSessao) {
        List<Bug> bugs = new ArrayList<>();
        String sql = "select id_bug, id_sessao, descricao, data, resolvido from Bug where id_sessao = ? order by data desc";
        try (Connection conexao = this.getConnection();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, idSessao);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                bugs.add(mapResultSetToBug(result));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar bugs por ID da sessão: " + e.getMessage(), e);
        }
        return bugs;
    }

    public void update(Bug bug) {
        String sql = "update Bug set descricao = ?, resolvido = ? where id_bug = ?";
        Connection conexao = null;
        try {
            conexao = this.getConnection();
            conexao.setAutoCommit(false);
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setString(1, bug.getDescricao());
                statement.setBoolean(2, bug.isResolvido());
                statement.setLong(3, bug.getId());
                int linhasAfetadas = statement.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("ID não encontrado: " + bug.getId());
                }
            }
            conexao.commit();
        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao fazer rollback da atualização do bug: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Erro ao atualizar bug: " + e.getMessage(), e);
        } finally {
            if (conexao != null) {
                try {
                    conexao.setAutoCommit(true);
                    conexao.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public void delete(Long id) {
        String sql = "delete from Bug where id_bug = ?";
        try (Connection conexao = this.getConnection();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar bug: " + e.getMessage(), e);
        }
    }
}