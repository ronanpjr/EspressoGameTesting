package br.ufscar.dc.dsw.dao;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Usuario;
//import br.ufscar.dc.dsw.domain.SessaoDeTestes;

public class ProjetoDAO extends GenericDAO {

    public void insert(Projeto projeto) {
        String sql = "INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES (?, ?, ?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, projeto.getNome());
            statement.setString(2, projeto.getDescricao());
            statement.setDate(3, new java.sql.Date(projeto.getDataCriacao().getTime()));


            String membrosStr = projeto.getMembros().stream()
                    .map(m -> m.getId().toString())
                    .collect(Collectors.joining(","));
            statement.setString(4, membrosStr);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Projeto> getAll() {
        List<Projeto> listaProjetos = new ArrayList<>();
        String sql = "SELECT * FROM Projeto";

        try (Connection conn = this.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            UsuarioDAO usuarioDAO = new UsuarioDAO(); // Importante!

            while (rs.next()) {
                Long id = rs.getLong("id_projeto");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                Date dataCriacao = rs.getDate("data_criacao");
                String membrosStr = rs.getString("membros");

                List<Usuario> membros = new ArrayList<>();
                if (membrosStr != null && !membrosStr.isEmpty()) {
                    String[] ids = membrosStr.split(",");
                    for (String idStr : ids) {
                        try {
                            Long membroId = Long.parseLong(idStr.trim());
                            Usuario membro = usuarioDAO.getbyID(membroId);
                            if (membro != null) {
                                membros.add(membro);
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar ID inválido
                        }
                    }
                }

                Projeto projeto = new Projeto(nome, descricao, dataCriacao, membros, id);
                listaProjetos.add(projeto);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return listaProjetos;
    }


    public Projeto get(Long id) {
        Projeto projeto = null;
        String sql = "SELECT * FROM Projeto WHERE id_projeto = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                Date dataCriacao = rs.getDate("data_criacao");
                String membrosStr = rs.getString("membros");

                List<Usuario> membros = new ArrayList<>();
                if (membrosStr != null && !membrosStr.isEmpty()) {
                    String[] ids = membrosStr.split(",");
                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    for (String idStr : ids) {
                        try {
                            Long membroId = Long.parseLong(idStr.trim());
                            Usuario membro = usuarioDAO.getbyID(membroId);
                            if (membro != null) {
                                membros.add(membro);
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar ID inválido
                        }
                    }
                }

                projeto = new Projeto(nome, descricao, dataCriacao, membros, id);
            }

            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return projeto;
    }


    public void update(Projeto projeto) {
        String sql = "UPDATE Projeto SET nome = ?, descricao = ?, data_criacao = ?, membros = ? WHERE id_projeto = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, projeto.getNome());
            statement.setString(2, projeto.getDescricao());
            statement.setDate(3, new java.sql.Date(projeto.getDataCriacao().getTime()));


            String membrosStr = projeto.getMembros().stream()
                    .map(m -> m.getId().toString())
                    .collect(Collectors.joining(","));
            statement.setString(4, membrosStr);

            statement.setLong(5, projeto.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Projeto projeto) {
        String sql = "DELETE FROM Projeto WHERE id_projeto = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, projeto.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUsuarioMembro(Long idProjeto, Long idUsuario) {
        if (idProjeto == null || idUsuario == null) {
            return false;
        }
        Projeto projeto = this.get(idProjeto);
        if (projeto != null && projeto.getMembros() != null) {
            for (Usuario membro : projeto.getMembros()) {
                if (membro.getId() != null && membro.getId().equals(idUsuario)) {
                    return true;
                }
            }
        }
        return false;
    }
}
