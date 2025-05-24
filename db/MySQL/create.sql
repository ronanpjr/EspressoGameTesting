CREATE DATABASE EspressoGameTesting;

USE EspressoGameTesting;

-- Tabela de usuários (membros dos projetos)
CREATE TABLE Usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(256) NOT NULL,
    login VARCHAR(20) NOT NULL UNIQUE,
    senha VARCHAR(64) NOT NULL,
    papel VARCHAR(10),
    PRIMARY KEY (id)
);

-- Tabela de projetos
CREATE TABLE Projeto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(256) NOT NULL,
    descricao TEXT,
    data_criacao DATE,
    membros TEXT,
    PRIMARY KEY (id)
);

-- Dados de exemplo para usuários
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Alice Testadora', 'alice', 'alice123', 'USER');
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Bob Dev', 'bob', 'bob123', 'USER');
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Carol Admin', 'carol', 'carol123', 'ADMIN');

-- Dados de exemplo para projetos
INSERT INTO Projeto(nome, descricao, data_criacao, membros)
VALUES (
    'Teste de Interface Espresso',
    'Projeto para testar interfaces Android com Espresso.',
    '2025-05-24',
    '1,2'
);

INSERT INTO Projeto(nome, descricao, data_criacao, membros)
VALUES (
    'Automação com Espresso',
    'Automação de testes em apps Android usando Espresso e JUnit.',
    '2025-05-15',
    '2,3'
);
