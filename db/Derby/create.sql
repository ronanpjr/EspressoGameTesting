connect 'jdbc:derby:EspressoGameTesting;create=true;user=root;password=root';

create table Usuario(id bigint not null generated always as identity, nome varchar(256) not null, login varchar(20) not null unique, senha varchar(64) not null, papel varchar(10), CONSTRAINT Usuario_PK PRIMARY KEY (id));

create table Projeto(
    id bigint not null generated always as identity,
    nome varchar(256) not null,
    descricao varchar(1000),
    data_criacao date,
    membros varchar(256),
    constraint Projeto_PK primary key (id)
);

insert into Usuario(nome, login, senha, papel) values ('Administrador', 'admin', 'admin', 'ADMIN');

insert into Usuario(nome, login, senha, papel) values ('Usuario', 'user', 'user', 'USER');

insert into Projeto(nome, descricao, data_criacao, membros) values (
    'Teste de Interface Espresso',
    'Projeto para testar interfaces Android com Espresso.',
    current_date,
    '1,2'
);

insert into Projeto(nome, descricao, data_criacao, membros) values (
    'Automação com Espresso',
    'Automação de testes em apps Android usando Espresso e JUnit.',
    current_date,
    '2,3'
);

disconnect;

quit;
