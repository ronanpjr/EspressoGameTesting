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


insert into Usuario(nome, login, senha, papel) values ('Administrador', 'admin@email.com', 'admin', 'admin');

insert into Usuario(nome, login, senha, papel) values ( 'Usuario', 'user@email.com', 'user', 'tester');


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
                               id_usuario BIGINT NOT NULL,
                               FOREIGN KEY (id_projeto) REFERENCES Projeto(id_projeto) ON DELETE CASCADE,
                               FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
                               UNIQUE KEY (id_projeto, id_usuario)

) ENGINE=InnoDB;

CREATE TABLE estrategias (
                             id_estrategia INT AUTO_INCREMENT PRIMARY KEY,
                             nome VARCHAR(255) NOT NULL UNIQUE,
                             descricao TEXT
) ENGINE=InnoDB;


CREATE TABLE exemplos (
                          id_exemplo INT AUTO_INCREMENT PRIMARY KEY,
                          id_estrategia INT NOT NULL,
                          texto TEXT NOT NULL,
                          url_imagem VARCHAR(1024),
                          FOREIGN KEY (id_estrategia) REFERENCES estrategias(id_estrategia) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE dicas_estrategia (
                                  id_dica INT AUTO_INCREMENT PRIMARY KEY,
                                  id_estrategia INT NOT NULL,
                                  dica TEXT NOT NULL,
                                  FOREIGN KEY (id_estrategia) REFERENCES estrategias(id_estrategia) ON DELETE CASCADE
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
                        FOREIGN KEY (id_tester) REFERENCES Usuario(id_usuario),
                        FOREIGN KEY (id_estrategia) REFERENCES estrategias(id_estrategia)
) ENGINE=InnoDB;

CREATE TABLE HistoricoStatusSessao (
                                       id_historico INT AUTO_INCREMENT PRIMARY KEY,
                                       id_sessao INT NOT NULL,
                                       status_anterior ENUM('created','in_execution','finalized'),
                                       status_novo ENUM('created','in_execution','finalized') NOT NULL,
                                       data_hora DATETIME NOT NULL,
                                       FOREIGN KEY (id_sessao) REFERENCES Sessao(id_sessao) ON DELETE CASCADE
) ENGINE=InnoDB;


INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Yohan Duarte', 'yduarte@email.com', 'testerpass', 'tester');
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Cristiano Politowski', 'cpolitowski@email.com', 'testerpass', 'tester');
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('André Takeshi Endo', 'aendo@email.com', 'testerpass', 'admin');
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Henrique Canella Mandelli', 'hmandelli@email.com', 'testerpass', 'tester');
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Vinicius Durelli', 'vdurelli@email.com', 'testerpass', 'tester');
INSERT INTO Usuario(nome, login, senha, papel) VALUES ('Paulo Augusto Nardi', 'pnardi@email.com', 'testerpass', 'tester');

INSERT INTO estrategias (nome, descricao) VALUES
('Newbie Journey (XPloiT)', 'Tester plays through the available game segment for the first time to learn about its mechanics, controls, balance, design, and objectives. Avoid spending excessive time exploring. After completing the initial playthrough, the tester will have enough experience to decide on the number of sessions and time constraints for later strategies.');
INSERT INTO estrategias (nome, descricao) VALUES
('Golden Path (XPloiT)', 'Testers play the game as intended by the designers and keep their actions straightforward, following the main or default path. May briefly stop to investigate features of particular interest. Developed by merging Tour Bus strategy, Coach Potato tour, and Golden Path concept.');
INSERT INTO estrategias (nome, descricao) VALUES
('Noob Journey (XPloiT)', 'Testers ignore the game''s tips and requirements and explore alternative paths to reach objectives, exploring unused or less common paths and features. Developed by merging Antisocial tour, Back Alley tour, and Blockaded Taxicab tour.');
INSERT INTO estrategias (nome, descricao) VALUES
('Completionist (XPloiT)', 'Tester tries to collect every possible output from the game (achievements, items, rewards, scores, upgrades), following the longest path. Developed by merging Collectors Tour, Lonely Businessman Tour, and Completionist game concept.');
INSERT INTO estrategias (nome, descricao) VALUES
('Stress Test (XPloiT)', 'Testers explore unexpected, invalid, or illegal inputs and actions, repeating them to identify potential issues. Developed by merging Antisocial Tour and OCD Tour.');
INSERT INTO estrategias (nome, descricao) VALUES
('Speedrun (XPloiT)', 'Testers learn about paths and objectives, set new objectives, and attempt to reach them using the shortest available path, aiming to achieve goals quickly by ignoring requirements or attempting alternate paths. Developed by merging Speedrun concept, Garbage Collectors Tour, Taxicab Tour, Landmark Tour, Coach Potato Tour, and Blockaded Taxicab Tour.');
INSERT INTO estrategias (nome, descricao) VALUES
('User Interface Strategy (XPloiT)', 'Testers focus on the user interface and localization aspects of the game test segment. Developed by merging User Interface Exploration, Supermodel Tour, and Multicultural Tour.');
INSERT INTO estrategias (nome, descricao) VALUES
('Neighboring Strategy (XPloiT)', 'Testers explore areas surrounding bugs, investigate potential consequences, and examine nearby features of commonly used elements with potential vulnerabilities. Developed by merging Bad Neighborhood Tour and Supporting Actor Tour.');
INSERT INTO estrategias (nome, descricao) VALUES
('Overtime Strategy (XPloiT)', 'Testers address all the questions or possible test cases they have noted and perform random tests whenever they think of something new to explore. Iterative with Neighboring strategy. Developed by merging Exploratory Smoke Testing and Intellectual Tour.');
INSERT INTO estrategias (nome, descricao) VALUES
('Tour Bus Strategy (Whittaker)', 'The tester takes a "tour" of the system, stopping at any feature as desired for a short period of time and then returning to the main route.');
INSERT INTO estrategias (nome, descricao) VALUES
('Exploratory Smoke Testing (Whittaker)', 'The tester randomly checks if the system''s features are functioning properly, without following a pattern or rules.');
INSERT INTO estrategias (nome, descricao) VALUES
('Crime Spree Tour (Whittaker)', 'The tester focuses on a specific feature or neighborhood with the intention of breaking it.');
INSERT INTO estrategias (nome, descricao) VALUES
('Garbage Collectors Tour (Whittaker)', 'The tester selects an objective, finds the fastest way to accomplish it, performs tests, and moves on to the next objective.');
INSERT INTO estrategias (nome, descricao) VALUES
('Back Alley Tour (Whittaker)', 'The tester focuses on exploring the less frequently used features of the system.');
INSERT INTO estrategias (nome, descricao) VALUES
('User Interface Exploration (Whittaker)', 'The tester learns by exploring the user interface, understanding the functionalities of different parts of the interface, and testing their behaviors.');
INSERT INTO estrategias (nome, descricao) VALUES
('Bad Neighborhood Tour (Whittaker)', 'The tester examines the "neighboring" features of where a bug was found, aiming to uncover other problems.');

INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
('Leap Hero', 'Platform game where players control a knight/frog to rescue a princess, using wall jumping and tongue grabbing. Demo assessed using XPloiT framework.', '2023-10-15', '3');
INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
('XPloiT Indie Platformer', 'A platform game under development by an indie studio, used for initial evaluation of the XPloiT framework.', '2023-01-01', '3');
INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
('Little Spy', '2D platform game (Little Spy-v1.0.6, released 2021) where a spy collects intelligence items and returns to a helicopter within a time limit. Tested using Whittaker ET strategies.', '2021-01-01', '3,6');
INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
('Diver Down', '2D platform game (Diver Down v1.2, released 2018) where players dive onto solids to progress through stages, avoiding lights. Tested using Whittaker ET strategies.', '2018-01-01', '3,6');
INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
('Tiny Crate', '2D platform game (Tiny Crate - v11.2022, released 2021) where players lift and toss crates to create platforms and reach higher ground. Tested using Whittaker ET strategies.', '2021-01-01', '3,6');
INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
('Portal', '3D puzzle-platform game (Portal-v12.2014, released 2007) where the player uses a portal gun to navigate test chambers. Tested using Whittaker ET strategies.', '2007-10-10', '3,6');
INSERT INTO Projeto (nome, descricao, data_criacao, membros) VALUES
('Portal 2', 'Sequel to Portal (Portal 2 v01.2022, released 2011), 3D puzzle-platform game with expanded mechanics. Tested using Whittaker ET strategies.', '2011-04-19', '3,6');

INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (1, 3);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (2, 3);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (3, 3);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (3, 6);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (4, 3);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (4, 6);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (5, 3);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (5, 6);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (6, 3);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (6, 6);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (7, 3);
INSERT INTO MembroProjeto (id_projeto, id_usuario) VALUES (7, 6);

INSERT INTO dicas_estrategia (id_estrategia, dica) VALUES
(1, 'Focus on general understanding: mechanics, controls, abilities, scoring, enemies.'),
(1, 'Use tutorials and help menus.'),
(1, 'Avoid diverting from the main path; progress to game''s end.'),
(1, 'Avoid extensive exploration of specific locations (save for other strategies).'),
(1, 'Tester defines the number of sessions and time constraints for subsequent strategies (S2) to (S7).'),
(7, 'Consider number of menus and UI elements (inputs, buttons, comboboxes) for session planning.'),
(7, 'Consider if game state influences UIs available.'),
(7, 'Test graphical settings, keybinds, sounds, consoles, HUDs, status bars.'),
(8, 'Tester evaluates the bugs found based on the applicability of their neighbouring features and possible consequences.'),
(9, 'Unexplored notes or ideas from previous steps are executed.'),
(13, 'Draw inspiration from speedrunning: achieve a specific goal ASAP.'),
(13, 'Focus on the objective, disregarding other parts, in a rapid way.'),
(10, 'Share similarities with completionist: explore comprehensively (all achievements, quests).'),
(14, 'Focus on less frequently utilized features from player''s standpoint (hidden elements, developer options, consoles).'),
(12, 'Explore negative scenarios around specific game features.'),
(12, 'Try applying mechanics out of established context/bounds or push limits of features.'),
(11, 'Do not follow guidelines besides randomly exploring game features.'),
(11, 'Use to answer "what if" questions about game features, often from previous session observations.'),
(16, 'The neighborhood of a bug may house other bugs; explore if faulty behavior can be partially replicated in different game states.'),
(16, 'Evaluate bugs beforehand and assess if there is a logical neighborhood to explore.');

INSERT INTO exemplos (id_estrategia, texto, url_imagem) VALUES
(1, 'Main character (1) needs to reach objective (4). Tester focuses on proceeding directly to the objective without spending much time on items (3) or enemies (2) (blue arrow). May briefly stop to investigate features of particular interest (cyan arrow).', 'Reference to Figure 2, PDF: Towards a framework for exploratory testing in video games.pdf, Page 3'),
(2, 'Tester focuses on reaching the objective by avoiding the expected path (blue arrow) and attempts alternative methods without meeting usual requirements (cyan arrow). Game tips and guides are disregarded.', 'Reference to Figure 3, PDF: Towards a framework for exploratory testing in video games.pdf, Page 3'),
(3, 'Tester explores the longest path (blue arrow) to reach the objective, defeating every enemy (red circles) and collecting all items (pink circles). Considers exploring the map (cyan arrow) for secrets.', 'Reference to Figure 4, PDF: Towards a framework for exploratory testing in video games.pdf, Page 3'),
(4, 'Game specifies objectives and controls. Tester should attempt other buttons, even if not obvious. If progression is by reaching helicopter, tester considers moving in opposite direction for alternative outcomes.', 'Reference to Figure 5, PDF: Towards a framework for exploratory testing in video games.pdf, Page 4'),
(5, 'Tester considers faster paths to objective (blue arrow), exploring climbing platforms, shortcuts (cyan arrow), or exploiting bugs. Example: benefit from character spawn to avoid lower platform, or find faster path eliminating a jump (Figure 7).', 'Reference to Figures 6 & 7, PDF: Towards a framework for exploratory testing in video games.pdf, Page 4'),
(1, 'Bug example: In windowed mode, enlarging "Achievements" pop-up blocked menu interactions.', NULL),
(2, 'Example: Saving game during developer commentary restarted it for remaining time. Led to check NPC dialogues, finding: "Subtitles disappear when game saved/reloaded during dialogue," and "Voice echoes when game saved/reloaded during dialogue."', NULL),
(3, 'Example: Game timer (top-right of Figs 2-7), if reaches "000", player loses. Question: "What happens if I enter helicopter just as timer reaches zero?" Tested for outcomes.', 'Reference to Figures 2-7, PDF: Towards a framework for exploratory testing in video games.pdf');

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
(1, 3, 1, '00:57:44', 'Newbie Journey (Single Session in source) on Leap Hero. 1 session conducted.', 'finalized');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(1, NULL, 'created', '2024-05-01 10:00:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(1, 'created', 'in_execution', '2024-05-01 10:01:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(1, 'in_execution', 'finalized', '2024-05-01 10:58:44');

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
(1, 3, 2, '00:37:27', 'Golden Path Strategy on Leap Hero. 2 sessions conducted.', 'finalized');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(2, NULL, 'created', '2024-05-02 11:00:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(2, 'created', 'in_execution', '2024-05-02 11:01:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(2, 'in_execution', 'finalized', '2024-05-02 11:38:27');

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
(1, 3, 8, '00:54:54', 'Neighboring Strategy on Leap Hero. 14 sessions conducted.', 'finalized');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(3, NULL, 'created', '2024-05-03 14:00:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(3, 'created', 'in_execution', '2024-05-03 14:01:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(3, 'in_execution', 'finalized', '2024-05-03 14:55:54');

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
(3, 3, 1, '00:09:17', 'Single Session Gameplay (mapped to Newbie Journey) on Little Spy (Game 1). 1 session.', 'finalized');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(4, NULL, 'created', '2024-05-04 09:00:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(4, 'created', 'in_execution', '2024-05-04 09:00:30');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(4, 'in_execution', 'finalized', '2024-05-04 09:09:47');

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
(3, 6, 10, '00:06:37', 'Tour Bus Strategy (Whittaker) on Little Spy (Game 1). 1 session.', 'finalized');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(5, NULL, 'created', '2024-05-04 10:00:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(5, 'created', 'in_execution', '2024-05-04 10:00:30');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(5, 'in_execution', 'finalized', '2024-05-04 10:07:07');

INSERT INTO Sessao (id_projeto, id_tester, id_estrategia, duracao, descricao, status) VALUES
(3, 3, 14, '00:04:59', 'Back Alley Tour (Whittaker) on Little Spy (Game 1). 1 session.', 'finalized');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(6, NULL, 'created', '2024-05-04 11:00:00');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(6, 'created', 'in_execution', '2024-05-04 11:00:30');
INSERT INTO HistoricoStatusSessao (id_sessao, status_anterior, status_novo, data_hora) VALUES
(6, 'in_execution', 'finalized', '2024-05-04 11:05:29');
