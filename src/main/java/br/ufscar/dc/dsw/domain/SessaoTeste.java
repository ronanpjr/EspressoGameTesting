package br.ufscar.dc.dsw.domain;

import br.ufscar.dc.dsw.util.StatusSessaoTeste;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class SessaoTeste {
    private Long id;
    private Long idProjeto;
    private Projeto projeto;
    private Long idTester;
    private Usuario tester;
    private Integer idEstrategia;
    private Estrategia estrategia;
    private Time duracao;
    private String descricao;
    private StatusSessaoTeste status;
    private List<HistoricoStatusSessaoTeste> historicoStatus;
    private List<Bug> bugs;

    public SessaoTeste() {
    }

    public SessaoTeste(Long idProjeto, Long idTester, Integer idEstrategia, Time duracao, String descricao) {
        this.idProjeto = idProjeto;
        this.idTester = idTester;
        this.idEstrategia = idEstrategia;
        this.duracao = duracao;
        this.descricao = descricao;
        this.status = StatusSessaoTeste.CREATED;
    }

    public SessaoTeste(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProjeto() {
        return idProjeto;
    }

    public void setIdProjeto(Long idProjeto) {
        this.idProjeto = idProjeto;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Long getIdTester() {
        return idTester;
    }

    public void setIdTester(Long idTester) {
        this.idTester = idTester;
    }

    public Usuario getTester() {
        return tester;
    }

    public void setTester(Usuario tester) {
        this.tester = tester;
    }

    public Integer getIdEstrategia() {
        return idEstrategia;
    }

    public void setIdEstrategia(Integer idEstrategia) {
        this.idEstrategia = idEstrategia;
    }

    public Estrategia getEstrategia() {
        return estrategia;
        }

    public void setEstrategia(Estrategia estrategia) {
        this.estrategia = estrategia;
    }

    public Time getDuracao() {
        return duracao;
    }

    public void setDuracao(Time duracao) {
        this.duracao = duracao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusSessaoTeste getStatus() {
        return status;
    }

    public void setStatus(StatusSessaoTeste status) {
        this.status = status;
    }

    public List<HistoricoStatusSessaoTeste> getHistoricoStatus() {
        return historicoStatus;
    }

    public void setHistoricoStatus(List<HistoricoStatusSessaoTeste> historicoStatus) {
        this.historicoStatus = historicoStatus;
    }

    public List<Bug> getBugs() {
        return bugs;
    }

    public void setBugs(List<Bug> bugs) {
        this.bugs = bugs;
    }
}

