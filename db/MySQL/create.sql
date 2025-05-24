-- Exclui o banco de dados se ele já existir (CUIDADO: Isso apagará todos os dados)
DROP DATABASE IF EXISTS EspressoTestingDB;

-- Cria o banco de dados (se não existir)
CREATE DATABASE IF NOT EXISTS EspressoTestingDB;

-- Seleciona o banco de dados para uso
USE EspressoTestingDB;


-- USUÁRIOS
CREATE TABLE Usuario (
                         id_usuario     bigint AUTO_INCREMENT PRIMARY KEY,
                         nome           VARCHAR(256) NOT NULL,
                         login          VARCHAR(255) NOT NULL UNIQUE,
                         senha          VARCHAR(255) NOT NULL,
                         papel          ENUM('admin','tester') NOT NULL
) ENGINE=InnoDB;


insert into Usuario(nome, login, senha, papel) values ('Administrador', 'admin', 'admin', 'admin');

insert into Usuario(nome, login, senha, papel) values ( 'Usuario', 'user', 'user', 'tester');


CREATE TABLE Projeto (
                         id_projeto INT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL UNIQUE,
                         descricao TEXT,
                         data_criacao DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE MembroProjeto (
                               id_membro INT AUTO_INCREMENT PRIMARY KEY,
                               id_projeto INT NOT NULL,
                               id_usuario BIGINT NOT NULL, -- Modificado para BIGINT para corresponder a Usuario.id_usuario
                               FOREIGN KEY (id_projeto) REFERENCES Projeto(id_projeto) ON DELETE CASCADE,
                               FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
                               UNIQUE KEY (id_projeto, id_usuario) -- Adicionado para evitar membros duplicados
) ENGINE=InnoDB;

CREATE TABLE Estrategia (
                            id_estrategia INT AUTO_INCREMENT PRIMARY KEY,
                            nome VARCHAR(255) NOT NULL UNIQUE,
                            descricao TEXT
) ENGINE=InnoDB;

CREATE TABLE Exemplo (
                         id_exemplo INT AUTO_INCREMENT PRIMARY KEY,
                         id_estrategia INT NOT NULL,
                         texto TEXT NOT NULL,
                         imagem_url VARCHAR(1024),
                         FOREIGN KEY (id_estrategia) REFERENCES Estrategia(id_estrategia) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE DicaEstrategia (
                                id_dica INT AUTO_INCREMENT PRIMARY KEY,
                                id_estrategia INT NOT NULL,
                                dica TEXT NOT NULL,
                                FOREIGN KEY (id_estrategia) REFERENCES Estrategia(id_estrategia) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Sessao (
                        id_sessao INT AUTO_INCREMENT PRIMARY KEY,
                        id_projeto INT NOT NULL,
                        id_tester BIGINT NOT NULL,
                        id_estrategia INT NOT NULL,
                        duracao TIME,
                        descricao TEXT,
                        status ENUM('created','in_execution','finalized') NOT NULL DEFAULT 'created',
                        FOREIGN KEY (id_projeto) REFERENCES Projeto(id_projeto) ON DELETE CASCADE,
                        FOREIGN KEY (id_tester) REFERENCES Usuario(id_usuario), -- ON DELETE SET NULL ou RESTRICT pode ser melhor aqui
                        FOREIGN KEY (id_estrategia) REFERENCES Estrategia(id_estrategia)
) ENGINE=InnoDB;

CREATE TABLE HistoricoStatusSessao (
                                       id_historico INT AUTO_INCREMENT PRIMARY KEY,
                                       id_sessao INT NOT NULL,
                                       status_anterior ENUM('created','in_execution','finalized'),
                                       status_novo ENUM('created','in_execution','finalized') NOT NULL,
                                       data_hora DATETIME NOT NULL,
                                       FOREIGN KEY (id_sessao) REFERENCES Sessao(id_sessao) ON DELETE CASCADE
) ENGINE=InnoDB;

