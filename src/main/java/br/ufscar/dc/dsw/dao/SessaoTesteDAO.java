package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.*;
import br.ufscar.dc.dsw.util.StatusSessaoTeste;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessaoTesteDAO extends GenericDAO {
    private void insertHistorico(Connection conexao, Long idSessao, String statusAnterior, String statusNovo)
            throws SQLException {
        String sql = "insert into HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) values (?, ?, ?, ?)";
        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, idSessao);
            if (statusAnterior == null) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, statusAnterior);
            }
            statement.setString(3, statusNovo);
            statement.setTimestamp(4, new Timestamp(new Date().getTime()));
            statement.executeUpdate();
        }
    }

    public void insert(SessaoTeste sessaoTeste) {
        String sql = "insert into Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) values (?, ?, ?, ?, ?, ?)";
        Connection conexao = null;
        try {
            conexao = this.getConnection();
            conexao.setAutoCommit(false);
            try (PreparedStatement statement = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, sessaoTeste.getIdProjeto());
                statement.setLong(2, sessaoTeste.getIdTester());
                statement.setLong(3, sessaoTeste.getIdEstrategia());
                statement.setTime(4, sessaoTeste.getDuracao());
                statement.setString(5, sessaoTeste.getDescricao());
                statement.setString(6, sessaoTeste.getStatus().getStatus());
                statement.executeUpdate();
                ResultSet resultado = statement.getGeneratedKeys();
                if (resultado.next()) {
                    sessaoTeste.setId(resultado.getLong(1));
                } else {
                    throw new SQLException("Nenhum ID obtido");
                }
            }
            insertHistorico(conexao, sessaoTeste.getId(), null, sessaoTeste.getStatus().getStatus());
            conexao.commit();
        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao fazer rollback de comandos de sessão: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Erro ao inserir sessão: " + e.getMessage(), e);
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

    private SessaoTeste mapResultSetToSessao(ResultSet rs) throws SQLException {
        SessaoTeste sessaoTeste = new SessaoTeste();
        sessaoTeste.setId(rs.getLong("id_sessao"));
        sessaoTeste.setIdProjeto(rs.getLong("id_projeto"));
        sessaoTeste.setIdTester(rs.getLong("id_tester"));
        sessaoTeste.setIdEstrategia(rs.getInt("id_estrategia"));
        sessaoTeste.setDuracao(rs.getTime("duracao"));
        sessaoTeste.setDescricao(rs.getString("descricao"));
        sessaoTeste.setStatus(StatusSessaoTeste.fromString(rs.getString("status")));
        Projeto projeto = new Projeto(sessaoTeste.getIdProjeto());
        projeto.setNome(rs.getString("nome_projeto"));
        sessaoTeste.setProjeto(projeto);
        Usuario tester = new Usuario(sessaoTeste.getIdTester());
        tester.setNome(rs.getString("nome_tester"));
        sessaoTeste.setTester(tester);
        Estrategia estrategia = new Estrategia();
        estrategia.setId(sessaoTeste.getIdEstrategia());
        estrategia.setNome(rs.getString("nome_estrategia"));
        sessaoTeste.setEstrategia(estrategia);
        return sessaoTeste;
    }

    public SessaoTeste getById(Long id) {
        SessaoTeste sessaoTeste = null;
        String sql = "select s.id_sessao, s.id_projeto, s.id_tester, s.id_estrategia, s.duracao, s.descricao, s.status, " +
                "p.nome as nome_projeto, u.nome as nome_tester, e.nome as nome_estrategia " +
                "from Sessao s " +
                "join Projeto p on s.id_projeto = p.id_projeto " +
                "join Usuario u on s.id_tester = u.id_usuario " +
                "join estrategias e on s.id_estrategia = e.id_estrategia " +
                "where s.id_sessao = ?";
        try (Connection conexao = this.getConnection();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                sessaoTeste = mapResultSetToSessao(rs);
                sessaoTeste.setHistoricoStatus(getHistoricoPorSessao(sessaoTeste.getId(), conexao));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessão por ID: " + e.getMessage(), e);
        }
        return sessaoTeste;
    }

    public List<SessaoTeste> getAllByProjetoId(Long idProjeto) {
        List<SessaoTeste> sessoes = new ArrayList<>();
        String sql = "select s.id_sessao, s.id_projeto, s.id_tester, s.id_estrategia, s.duracao, s.descricao, s.status, " +
                "p.nome as nome_projeto, u.nome as nome_tester, e.nome as nome_estrategia " +
                "from Sessao s " +
                "join Projeto p on s.id_projeto = p.id_projeto " +
                "join Usuario u on s.id_tester = u.id_usuario " +
                "join estrategias e on s.id_estrategia = e.id_estrategia " +
                "where s.id_projeto = ? order by s.id_sessao desc";
        try (Connection conexao = this.getConnection();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, idProjeto);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                sessoes.add(mapResultSetToSessao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessões por ID do projeto: " + e.getMessage(), e);
        }
        return sessoes;
    }

    public List<SessaoTeste> getAllByTesterId(Long idTester) {
        List<SessaoTeste> sessoes = new ArrayList<>();
        String sql = "select s.id_sessao, s.id_projeto, s.id_tester, s.id_estrategia, s.duracao, s.descricao, s.status, " +
                "p.nome as nome_projeto, u.nome as nome_tester, e.nome as nome_estrategia " +
                "from Sessao s " +
                "join Projeto p on s.id_projeto = p.id_projeto " +
                "join Usuario u on s.id_tester = u.id_usuario " +
                "join estrategias e on s.id_estrategia = e.id_estrategia " +
                "where s.id_tester = ? order by s.id_sessao desc";
        try (Connection conexao = this.getConnection();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, idTester);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                sessoes.add(mapResultSetToSessao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessões por ID do tester: " + e.getMessage(), e);
        }
        return sessoes;
    }

    public void updateStatus(Long idSessao, StatusSessaoTeste novoStatusEnum) {
        String sqlSelect = "select status from Sessao where id_sessao = ?";
        String sqlUpdate = "update Sessao set status = ? where id_sessao = ?";
        Connection conexao = null;
        try {
            conexao = this.getConnection();
            conexao.setAutoCommit(false);
            String statusAnterior = null;
            try (PreparedStatement statementSelect = conexao.prepareStatement(sqlSelect)) {
                statementSelect.setLong(1, idSessao);
                ResultSet result = statementSelect.executeQuery();
                if (result.next()) {
                    statusAnterior = result.getString("status");
                } else {
                    throw new SQLException("Sessão não encontrada");
                }
            }
            try (PreparedStatement statementUpdate = conexao.prepareStatement(sqlUpdate)) {
                statementUpdate.setString(1, novoStatusEnum.getStatus());
                statementUpdate.setLong(2, idSessao);
                int linhasAfetadas = statementUpdate.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("Erro em atualizar status da sessão");
                }
            }
            insertHistorico(conexao, idSessao, statusAnterior, novoStatusEnum.getStatus());
            conexao.commit();
        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao fazer rollback da atualização de status: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Erro ao atualizar status da sessão: " + e.getMessage(), e);
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

    public List<HistoricoStatusSessaoTeste> getHistoricoPorSessao(Long idSessao) {
        return getHistoricoPorSessao(idSessao, null);
    }

    private List<HistoricoStatusSessaoTeste> getHistoricoPorSessao(Long idSessao, Connection conexaoExistente) {
        List<HistoricoStatusSessaoTeste> historico = new ArrayList<>();
        String sql = "select id_historico, id_sessao, status_anterior, status_novo, data_hora " +
                "from HistoricoStatusSessao where id_sessao = ? order by data_hora asc";
        Connection conexao = null;
        try {
            conexao = (conexaoExistente != null) ? conexaoExistente : this.getConnection();
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setLong(1, idSessao);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    historico.add(new HistoricoStatusSessaoTeste(
                            rs.getLong("id_historico"),
                            rs.getLong("id_sessao"),
                            rs.getString("status_anterior"),
                            rs.getString("status_novo"),
                            rs.getTimestamp("data_hora")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico da sessão: " + e.getMessage(), e);
        } finally {
            if (conexaoExistente == null && conexao != null) {
                try {
                    conexao.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return historico;
    }

    public void delete(Long idSessao) {
        String sql = "delete from Sessao where id_sessao = ?";
        try (Connection conexao = this.getConnection();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, idSessao);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar sessão: " + e.getMessage(), e);
        }
    }

    public void updateSessao(SessaoTeste sessaoTeste, StatusSessaoTeste novoStatus) {
        String sql = "update Sessao set duracao = ?, descricao = ?, status = ? where id_sessao = ?";
        Connection conexao = null;
        try {
            conexao = this.getConnection();
            conexao.setAutoCommit(false);
            String statusAnterior = null;
            String sqlSelectStatus = "select status from Sessao where id_sessao = ?";
            try (PreparedStatement statementSelect = conexao.prepareStatement(sqlSelectStatus)) {
                statementSelect.setLong(1, sessaoTeste.getId());
                ResultSet rs = statementSelect.executeQuery();
                if (rs.next()) {
                    statusAnterior = rs.getString("status");
                } else {
                    throw new SQLException("Sessão não encontrada para atualização, ID: " + sessaoTeste.getId());
                }
            }
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setTime(1, sessaoTeste.getDuracao());
                statement.setString(2, sessaoTeste.getDescricao());
                statement.setString(3, novoStatus.getStatus());
                statement.setLong(4, sessaoTeste.getId());
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Atualização da sessão falhou");
                }
            }
            if (statusAnterior != null && !statusAnterior.equals(novoStatus.getStatus())) {
                insertHistorico(conexao, sessaoTeste.getId(), statusAnterior, novoStatus.getStatus());
            }
            conexao.commit();
        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Erro ao fazer rollback da atualização da sessão: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Erro ao atualizar sessão: " + e.getMessage(), e);
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
}