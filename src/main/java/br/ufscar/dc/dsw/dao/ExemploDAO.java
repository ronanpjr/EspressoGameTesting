package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.Exemplo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExemploDAO extends GenericDAO {

    public ExemploDAO() {
        super();
    }

    public void salvar(Exemplo exemplo, Connection conn) throws SQLException {
        String sql = "INSERT INTO exemplos (id_estrategia, texto, atributo1, url_imagem) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, exemplo.getIdEstrategia());
            stmt.setString(2, exemplo.getTexto());
            if (exemplo.getAtributo1() != null) {
                stmt.setInt(3, exemplo.getAtributo1());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            if (exemplo.getUrlImagem() != null && !exemplo.getUrlImagem().trim().isEmpty()) {
                stmt.setString(4, exemplo.getUrlImagem());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    exemplo.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Exemplo> listarPorEstrategiaId(int idEstrategia) {
        List<Exemplo> exemplos = new ArrayList<>();
        String sql = "SELECT id_exemplo, texto, atributo1, url_imagem FROM exemplos WHERE id_estrategia = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstrategia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Exemplo exemplo = new Exemplo();
                    exemplo.setId(rs.getInt("id_exemplo"));
                    exemplo.setIdEstrategia(idEstrategia);
                    exemplo.setTexto(rs.getString("texto"));
                    int atributo1Val = rs.getInt("atributo1");
                    if (!rs.wasNull()) {
                        exemplo.setAtributo1(atributo1Val);
                    } else {
                        exemplo.setAtributo1(null);
                    }

                    exemplo.setUrlImagem(rs.getString("url_imagem"));
                    exemplos.add(exemplo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar exemplos por ID da estrategia: " + e.getMessage());
        }
        return exemplos;
    }

    public void excluirPorEstrategiaId(int idEstrategia, Connection conn) throws SQLException {
        String sql = "DELETE FROM exemplos WHERE id_estrategia = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstrategia);
            stmt.executeUpdate();
        }
    }
}