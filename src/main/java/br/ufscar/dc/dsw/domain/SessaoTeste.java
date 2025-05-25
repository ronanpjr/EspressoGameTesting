package br.ufscar.dc.dsw.domain;

import br.ufscar.dc.dsw.util.StatusSessaoTeste;

import java.sql.Time;
import java.util.List;

public class SessaoTeste {
    private Long id;
    private Long idProjeto;
    private Projeto projeto;
    private Long idTester;
    private Usuario tester;
    private Long idEstrategia;
    // private Estrategia estrategia;
    private Time duracao;
    private String descricao;
    private StatusSessaoTeste status;
    private List<HistoricoStatusSessaoTeste> historicoStatus;

    public SessaoTeste() {
    }

    public SessaoTeste(Long idProjeto, Long idTester, Long idEstrategia, Time duracao, String descricao) {
        this.idProjeto = idProjeto;
        this.idTester = idTester;
        this.idEstrategia = idEstrategia;
        this.duracao = duracao;
        this.descricao = descricao;
        this.status = StatusSessaoTeste.CREATED; // Default status using enum
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

    public Long getIdEstrategia() {
        return idEstrategia;
    }

    public void setIdEstrategia(Long idEstrategia) {
        this.idEstrategia = idEstrategia;
    }

//    public Estrategia getEstrategia() {
//        return estrategia;
//    }
//
//    public void setEstrategia(Estrategia estrategia) {
//        this.estrategia = estrategia;
//    }

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

    public StatusSessaoTeste getStatus() { // 3. Getter returns StatusSessao
        return status;
    }

    public void setStatus(StatusSessaoTeste status) { // 4. Setter accepts StatusSessao
        this.status = status;
    }

    // Convenience method if you still need to set status from a string (e.g., from DB)
    public void setStatusFromString(String statusStr) {
        this.status = StatusSessaoTeste.fromString(statusStr);
    }

    public List<HistoricoStatusSessaoTeste> getHistoricoStatus() {
        return historicoStatus;
    }

    public void setHistoricoStatus(List<HistoricoStatusSessaoTeste> historicoStatus) {
        this.historicoStatus = historicoStatus;
    }
}

