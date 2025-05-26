package br.ufscar.dc.dsw.util;

public enum StatusSessaoTeste {
    CREATED("created"),
    IN_EXECUTION("in_execution"),
    FINALIZED("finalized");

    private final String status;

    StatusSessaoTeste(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static StatusSessaoTeste fromString(String text) {
        for (StatusSessaoTeste b : StatusSessaoTeste.values()) {
            if (b.status.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Status inválido");
    }
}
