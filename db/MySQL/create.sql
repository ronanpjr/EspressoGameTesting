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
                         data_criacao DATE NOT NULL,
                         membros TEXT NOT NULL
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


INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Ana Testadora', 'ana', 'ana123', 'tester');

INSERT INTO Estrategia (nome, descricao) VALUES
                                             ('Exploração Baseada em Roteiro', 'Segue um conjunto predefinido de passos ou cenários de usuário para testar funcionalidades específicas.'),
                                             ('Testes Ad-hoc', 'Testes informais e improvisados, sem planejamento ou documentação formal, baseados na intuição e experiência do testador.'),
                                             ('Caça-Bugs Competitivo', 'Vários testadores competem para encontrar o maior número de bugs ou os bugs mais críticos em um período de tempo limitado.'),
                                             ('Teste de Personas', 'O testador assume diferentes perfis de jogadores (ex: novato, experiente, casual, hardcore) para explorar o jogo sob diversas perspectivas.');

INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
                                                                 ('A Lenda da Espada Perdida', 'RPG de ação em mundo aberto com temática medieval fantástica.', '2024-01-15', '1,2'),
                                                                 ('Conquista Estelar X', 'Jogo de estratégia 4X espacial com múltiplas facções e customização de naves.', '2024-03-10', '2,3'),
                                                                 ('A Fazendinha Feliz', 'Simulador de fazenda casual com foco em plantio, colheita e interação com animais.', CURDATE(), '1,2,3');

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
    (1, 2, 1, '01:30:00', 'Testar a sequência de quests iniciais da vila principal, seguindo o roteiro de progressão do jogador.', 'created');                                                                                                                                                        INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
    (1, NULL, 'created', NOW());

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
    (1, 3, 2, '02:00:00', 'Exploração livre do mapa da floresta inicial, tentando interagir com todos os elementos e NPCs de formas não usuais.', 'in_execution');

INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
    (2, NULL, 'created', DATE_SUB(NOW(), INTERVAL 2 HOUR));

INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
    (2, 'created', 'in_execution', DATE_SUB(NOW(), INTERVAL 1 HOUR));

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
    (2, 2, 4, '01:15:00', 'Jogar como um "Mercador Pacifista", focando em rotas de comércio e diplomacia, evitando combate.', 'finalized');

INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
    (3, NULL, 'created', DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
    (3, 'created', 'in_execution', DATE_SUB(NOW(), INTERVAL 20 HOUR));

INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
    (3, 'in_execution', 'finalized', DATE_SUB(NOW(), INTERVAL 18 HOUR));

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
    (3, 1, 3, '00:45:00', 'Sessão rápida para tentar encontrar o maior número de bugs visuais na interface de plantio.', 'created');

INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
    (4, NULL, 'created', NOW());