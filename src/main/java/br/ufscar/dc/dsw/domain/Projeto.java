package br.ufscar.dc.dsw.domain;

import java.util.Date;
import java.util.List;

public class Projeto {
    private Long id;
    private String nome;
    private String descricao;
    private Date dataCriacao;
    private List<Usuario> membros;
    //private List<SessaoDeTeste> sessaoDeTestes;

    public Projeto(String nome, String descricao, Date dataCriacao, List<Usuario> membros) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.membros = membros;
    }

    public Projeto(Long id) {
        this.id = id;
    }

    public Projeto(String nome, String descricao, Date dataCriacao, List<Usuario> membros, Long id) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.membros = membros;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
