package br.ufscar.dc.dsw.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Bug {
    private Long id;
    private Long idSessao;
    private String descricao;
    private LocalDateTime data;
    private boolean resolvido;

    public Bug() {
    }

    public Bug(Long idSessao, String descricao, LocalDateTime data) {
        this.idSessao = idSessao;
        this.descricao = descricao;
        this.data = data;
        this.resolvido = false;
    }

    public Bug(Long id, Long idSessao, String descricao, LocalDateTime data, boolean resolvido) {
        this.id = id;
        this.idSessao = idSessao;
        this.descricao = descricao;
        this.data = data;
        this.resolvido = resolvido;
    }

    public Bug(Long id) {
        this.id = id;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Timestamp getDataAsTimestamp() {
        if (this.data == null) {
            return null;
        }
        return Timestamp.valueOf(this.data);
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public boolean isResolvido() {
        return resolvido;
    }

    public void setResolvido(boolean resolvido) {
        this.resolvido = resolvido;
    }
}