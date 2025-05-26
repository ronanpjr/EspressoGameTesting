package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.DicaEstrategia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DicaEstrategiaDAO extends GenericDAO {

    public DicaEstrategiaDAO() {
        super();
    }

    public void salvar(DicaEstrategia dica, Connection conn) throws SQLException {
        String sql = "INSERT INTO dicas_estrategia (id_estrategia, dica) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dica.getIdEstrategia());
            stmt.setString(2, dica.getDica());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    dica.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<DicaEstrategia> listarPorEstrategiaId(int idEstrategia) {
        List<DicaEstrategia> dicas = new ArrayList<>();
        String sql = "SELECT id_dica, dica FROM dicas_estrategia WHERE id_estrategia = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstrategia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DicaEstrategia dica = new DicaEstrategia();
                    dica.setId(rs.getInt("id_dica"));
                    dica.setIdEstrategia(idEstrategia);
                    dica.setDica(rs.getString("dica"));
                    dicas.add(dica);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar dicas: " + e.getMessage());
        }
        return dicas;
    }

    public void excluirPorEstrategiaId(int idEstrategia, Connection conn) throws SQLException {
        String sql = "DELETE FROM dicas_estrategia WHERE id_estrategia = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstrategia);
            stmt.executeUpdate();
        }
    }
}