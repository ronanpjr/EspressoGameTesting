create database Livraria;

use Livraria;

-- USUÁRIOS
CREATE TABLE Usuario (
                         id_usuario     bigint AUTO_INCREMENT PRIMARY KEY,
                         nome           VARCHAR(256) NOT NULL,
                         login          VARCHAR(255) NOT NULL UNIQUE,
                         senha          VARCHAR(255) NOT NULL,
                         papel          ENUM('admin','tester') NOT NULL
) ENGINE=InnoDB;


-- create table Usuario(id bigint not null auto_increment, nome varchar(256) not null, login varchar(20) not null unique, senha varchar(64) not null, papel varchar(10), primary key (id));

insert into Usuario(nome, login, senha, papel) values ('Administrador', 'admin', 'admin', 'admin');

insert into Usuario(nome, login, senha, papel) values ( 'Usuario', 'user', 'user', 'tester');



-- PROJETOS
CREATE TABLE projetos (
                          id_projeto     INT AUTO_INCREMENT PRIMARY KEY,
                          nome           VARCHAR(255) NOT NULL UNIQUE,
                          descricao      TEXT,
                          data_criacao   DATE        NOT NULL
) ENGINE=InnoDB;

-- MEMBROS DE PROJETO (N:N entre projetos e usuários)
CREATE TABLE membros_projeto (
                                 id_membro       INT AUTO_INCREMENT PRIMARY KEY,
                                 id_projeto      INT NOT NULL,
                                 id_usuario      BIGINT NOT NULL,
                                 FOREIGN KEY (id_projeto) REFERENCES projetos(id_projeto) ON DELETE CASCADE,
                                 FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ESTRATÉGIAS
CREATE TABLE estrategias (
                             id_estrategia  INT AUTO_INCREMENT PRIMARY KEY,
                             nome           VARCHAR(255) NOT NULL UNIQUE,
                             descricao      TEXT
) ENGINE=InnoDB;

-- EXEMPLOS (1:N de estratégia → exemplo)
CREATE TABLE exemplos (
                          id_exemplo      INT AUTO_INCREMENT PRIMARY KEY,
                          id_estrategia   INT         NOT NULL,
                          texto           TEXT        NOT NULL,
                          atributo1       INT,
                          url_imagem VARCHAR(512) NULL,
                          FOREIGN KEY (id_estrategia) REFERENCES estrategias(id_estrategia) ON DELETE CASCADE
) ENGINE=InnoDB;

-- DICAS DE ESTRATÉGIA (1:N de estratégia → dica)
CREATE TABLE dicas_estrategia (
                                  id_dica         INT AUTO_INCREMENT PRIMARY KEY,
                                  id_estrategia   INT         NOT NULL,
                                  dica            TEXT        NOT NULL,
                                  FOREIGN KEY (id_estrategia) REFERENCES estrategias(id_estrategia) ON DELETE CASCADE
) ENGINE=InnoDB;

-- SESSÕES
CREATE TABLE sessoes (
                         id_sessao       INT AUTO_INCREMENT PRIMARY KEY,
                         id_projeto      INT     NOT NULL,
                         id_tester       BIGINT     NOT NULL,
                         id_estrategia   INT     NOT NULL,
                         duracao         TIME,
                         descricao       TEXT,
                         status          ENUM('created','in_execution','finalized')
                                                 NOT NULL DEFAULT 'created',
                         FOREIGN KEY (id_projeto)    REFERENCES projetos(id_projeto)    ON DELETE CASCADE,
                         FOREIGN KEY (id_tester)     REFERENCES Usuario(id_usuario),
                         FOREIGN KEY (id_estrategia) REFERENCES estrategias(id_estrategia)
) ENGINE=InnoDB;

-- HISTÓRICO DE MUDANÇA DE STATUS
CREATE TABLE historico_status_sessao (
                                         id_historico    INT AUTO_INCREMENT PRIMARY KEY,
                                         id_sessao       INT     NOT NULL,
                                         data_hora       DATETIME NOT NULL,
                                         FOREIGN KEY (id_sessao) REFERENCES sessoes(id_sessao) ON DELETE CASCADE
) ENGINE=InnoDB;
