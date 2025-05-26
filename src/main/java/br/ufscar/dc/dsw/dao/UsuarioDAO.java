package br.ufscar.dc.dsw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.ufscar.dc.dsw.domain.Usuario;

public class UsuarioDAO extends GenericDAO {

    /**
     * Insere um novo usuário na base de dados.
     *
     * @param usuario O objeto Usuario a ser inserido.
     * @throws IllegalArgumentException se campos obrigatórios (nome, login, senha, papel) forem nulos ou vazios.
     * @throws RuntimeException se ocorrer um erro na base de dados, com uma mensagem amigável ao utilizador
     * (ex: login duplicado).
     */
    public void insert(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Objeto usuário não pode ser nulo.");
        }
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'nome' do usuário não pode ser nulo ou vazio.");
        }
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'login' do usuário não pode ser nulo ou vazio.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'senha' do usuário não pode ser nulo ou vazio.");
        }
        if (usuario.getPapel() == null || usuario.getPapel().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'papel' do usuário não pode ser nulo ou vazio.");
        }

        String sql = "INSERT INTO Usuario (nome, login, senha, papel) VALUES (?, ?, ?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getLogin());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getPapel());
            statement.executeUpdate();

        } catch (SQLException e) {
            // SQLState '23000' e '23505' são comuns para violações de restrição de integridade / violações únicas
            if ("23000".equals(e.getSQLState()) || "23505".equals(e.getSQLState()) ||
                    (e.getMessage() != null && (e.getMessage().toLowerCase().contains("unique constraint") || e.getMessage().toLowerCase().contains("duplicate key")))) {
                throw new RuntimeException("Erro ao inserir usuário: O login '" + usuario.getLogin() + "' já está cadastrado. Por favor, escolha outro login.", e);
            }
            // SQLState '23502' é comum para violação de não-nulo
            if ("23502".equals(e.getSQLState()) || (e.getMessage() != null && e.getMessage().toLowerCase().contains("null value in column"))){
                throw new RuntimeException("Erro ao inserir usuário: Um ou mais campos obrigatórios não foram preenchidos.", e);
            }
            throw new RuntimeException("Erro ao inserir usuário na base de dados. Detalhes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém todos os usuários da base de dados.
     *
     * @return Uma lista de objetos Usuario.
     * @throws RuntimeException se ocorrer um erro na base de dados.
     */
    public List<Usuario> getAll() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nome, login, senha, papel from Usuario u"; // Especificar colunas é uma boa prática

        try (Connection conn = this.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long id = resultSet.getLong("id_usuario");
                String nome = resultSet.getString("nome");
                String login = resultSet.getString("login");
                String senha = resultSet.getString("senha");
                String papel = resultSet.getString("papel");
                Usuario usuario = new Usuario(id, nome, login, senha, papel);
                listaUsuarios.add(usuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os usuários: " + e.getMessage(), e);
        }
        return listaUsuarios;
    }

    public void delete(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new IllegalArgumentException("Objeto usuário ou ID do usuário não pode ser nulo para exclusão.");
        }

        String sql = "DELETE FROM Usuario where id_usuario = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, usuario.getId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Erro ao remover usuário: Nenhum usuário encontrado com o ID " + usuario.getId() + ".");
            }
        } catch (SQLException e) {
            // SQLState '23000', '23503' são comuns para violações de chave estrangeira
            if ("23000".equals(e.getSQLState()) || "23503".equals(e.getSQLState()) ||
                    (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key constraint"))) {
                throw new RuntimeException("Erro ao remover usuário: Este usuário possui registos dependentes (ex: locações, propostas) e não pode ser excluído.", e);
            }
            throw new RuntimeException("Erro ao remover usuário da base de dados. Detalhes: " + e.getMessage(), e);
        }
    }


    public void update(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Objeto usuário não pode ser nulo para atualização.");
        }
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo para atualização.");
        }
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'nome' do usuário não pode ser nulo ou vazio para atualização.");
        }
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'login' do usuário não pode ser nulo ou vazio para atualização.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'senha' do usuário não pode ser nulo ou vazio para atualização.");
        }
        if (usuario.getPapel() == null || usuario.getPapel().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo 'papel' do usuário não pode ser nulo ou vazio para atualização.");
        }

        String sql = "UPDATE Usuario SET nome = ?, login = ?, senha = ?, papel = ? WHERE id_usuario = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getLogin());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getPapel());
            statement.setLong(5, usuario.getId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Erro ao atualizar usuário: Nenhum usuário encontrado com o ID " + usuario.getId() + ".");
            }
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState()) || "23505".equals(e.getSQLState()) ||
                    (e.getMessage() != null && (e.getMessage().toLowerCase().contains("unique constraint") || e.getMessage().toLowerCase().contains("duplicate key")))) {
                throw new RuntimeException("Erro ao atualizar usuário: O login '" + usuario.getLogin() + "' já está cadastrado para outro usuário. Por favor, escolha outro login.", e);
            }
            if ("23502".equals(e.getSQLState()) || (e.getMessage() != null && e.getMessage().toLowerCase().contains("null value in column"))){
                throw new RuntimeException("Erro ao atualizar usuário: Um ou mais campos obrigatórios não foram preenchidos.", e);
            }
            throw new RuntimeException("Erro ao atualizar dados do usuário. Detalhes: " + e.getMessage(), e);
        }
    }


    public Usuario getbyID(Long idParam) {
        Usuario usuario = null;
        String sql = "SELECT id_usuario, nome, login, senha, papel from Usuario WHERE id_usuario = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setLong(1, idParam);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // O idParam é o ID correto para o usuário encontrado.
                    String nome = resultSet.getString("nome");
                    String login = resultSet.getString("login");
                    String senha = resultSet.getString("senha");
                    String papel = resultSet.getString("papel");
                    usuario = new Usuario(idParam, nome, login, senha, papel);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário pelo ID: " + e.getMessage(), e);
        }
        return usuario;
    }

    public Usuario getbyLogin(String loginParam) {
        Usuario usuario = null;
        String sql = "SELECT id_usuario, nome, login, senha, papel from Usuario WHERE login = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, loginParam);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id_usuario");
                    String nome = resultSet.getString("nome");
                    String senha = resultSet.getString("senha");
                    String papel = resultSet.getString("papel");
                    usuario = new Usuario(id, nome, loginParam, senha, papel);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário pelo login: " + e.getMessage(), e);
        }
        return usuario;
    }
}
