package br.ufscar.dc.dsw.domain;

import java.util.Date;

public class HistoricoStatusSessaoTeste {
    private Long id;
    private Long idSessao;
    private String statusAnterior;
    private String statusNovo;
    private Date dataHora;

    public HistoricoStatusSessaoTeste(Long idSessao, String statusAnterior, String statusNovo, Date dataHora) {
        this.idSessao = idSessao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.dataHora = dataHora;
    }

    public HistoricoStatusSessaoTeste(Long id, Long idSessao, String statusAnterior, String statusNovo, Date dataHora) {
        this.id = id;
        this.idSessao = idSessao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(Long idSessao) {
        this.idSessao = idSessao;
    }

    public String getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(String statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public String getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(String statusNovo) {
        this.statusNovo = statusNovo;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }
}
