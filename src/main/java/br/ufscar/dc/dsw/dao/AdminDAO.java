package br.ufscar.dc.dsw.dao;

import java.sql.*;
import java.util.*;
import br.ufscar.dc.dsw.domain.Usuario;

public class AdminDAO {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/testes";
    private final String jdbcUsuario = "root";
    private final String jdbcSenha = "";

    private static final String SELECT_TODOS = "SELECT * FROM users WHERE papel = 'admin'";
    private static final String INSERIR = "INSERT INTO users (name, login, senha, papel) VALUES (?, ?, ?, 'admin')";
    private static final String SELECT_POR_ID = "SELECT * FROM users WHERE id = ?";
    private static final String ATUALIZAR = "UPDATE users SET name = ?, login = ?, senha = ? WHERE id = ?";
    private static final String DELETAR = "DELETE FROM users WHERE id = ?";

    protected Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsuario, jdbcSenha);
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conexao = obterConexao();
             PreparedStatement comando = conexao.prepareStatement(SELECT_TODOS);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                Usuario usuario = new Usuario(
                        resultado.getLong("id"),
                        resultado.getString("name"),
                        resultado.getString("login"),
                        resultado.getString("senha"),
                        resultado.getString("papel")
                );
                lista.add(usuario);
            }
        }
        return lista;
    }

    public void inserir(Usuario usuario) throws SQLException {
        try (Connection conexao = obterConexao();
             PreparedStatement comando = conexao.prepareStatement(INSERIR)) {
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getLogin());
            comando.setString(3, usuario.getSenha());
            comando.executeUpdate();
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        Usuario usuario = null;
        try (Connection conexao = obterConexao();
             PreparedStatement comando = conexao.prepareStatement(SELECT_POR_ID)) {
            comando.setInt(1, id);
            ResultSet resultado = comando.executeQuery();
            if (resultado.next()) {
                usuario = new Usuario(
                        (long) id,
                        resultado.getString("name"),
                        resultado.getString("login"),
                        resultado.getString("senha"),
                        resultado.getString("papel")
                );
            }
        }
        return usuario;
    }

    public void atualizar(Usuario usuario) throws SQLException {
        try (Connection conexao = obterConexao();
             PreparedStatement comando = conexao.prepareStatement(ATUALIZAR)) {
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getLogin());
            comando.setString(3, usuario.getSenha());
            comando.setLong(4, usuario.getId());
            comando.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        try (Connection conexao = obterConexao();
             PreparedStatement comando = conexao.prepareStatement(DELETAR)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }
}
