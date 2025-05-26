package br.ufscar.dc.dsw.domain;

import java.util.ArrayList;
import java.util.List;

public class Estrategia {
    private int id;
    private String nome;
    private String descricao;
    private List<Exemplo> exemplos;
    private List<DicaEstrategia> dicas;

    public Estrategia() {
        this.exemplos = new ArrayList<>();
        this.dicas = new ArrayList<>();
    }

    public Estrategia(int id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.exemplos = new ArrayList<>();
        this.dicas = new ArrayList<>();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Exemplo> getExemplos() {
        return exemplos;
    }

    public void setExemplos(List<Exemplo> exemplos) {
        this.exemplos = exemplos;
    }

    public List<DicaEstrategia> getDicas() {
        return dicas;
    }

    public void setDicas(List<DicaEstrategia> dicas) {
        this.dicas = dicas;
    }

    public void addExemplo(Exemplo exemplo) {
        this.exemplos.add(exemplo);
        exemplo.setIdEstrategia(this.id);
    }

    public void addDica(DicaEstrategia dica) {
        this.dicas.add(dica);
        dica.setIdEstrategia(this.id);
    }

    @Override
    public String toString() {
        return "Estrategia [id=" + id + ", nome=" + nome + ", descricao=" + descricao + "]";
    }
}