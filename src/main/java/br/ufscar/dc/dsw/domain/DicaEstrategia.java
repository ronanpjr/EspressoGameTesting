package br.ufscar.dc.dsw.domain;

public class DicaEstrategia {
    private int id;
    private int idEstrategia;
    private String dica;

    public DicaEstrategia() {}

    public DicaEstrategia(int id, int idEstrategia, String dica) {
        this.id = id;
        this.idEstrategia = idEstrategia;
        this.dica = dica;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstrategia() {
        return idEstrategia;
    }

    public void setIdEstrategia(int idEstrategia) {
        this.idEstrategia = idEstrategia;
    }

    public String getDica() {
        return dica;
    }

    public void setDica(String dica) {
        this.dica = dica;
    }

    @Override
    public String toString() {
        return "DicaEstrategia [id=" + id + ", idEstrategia=" + idEstrategia + ", dica=" + dica + "]";
    }
}