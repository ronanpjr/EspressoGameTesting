package br.ufscar.dc.dsw.domain;

public class Exemplo {
    private int id;
    private int idEstrategia;
    private String texto;
    private String urlImagem;

    public Exemplo() {}

    public Exemplo(int id, int idEstrategia, String texto, String urlImagem ) {
        this.id = id;
        this.idEstrategia = idEstrategia;
        this.texto = texto;
        this.urlImagem = urlImagem;

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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    @Override
    public String toString() {
        return "Exemplo [id=" + id + ", idEstrategia=" + idEstrategia + ", texto=" + texto +
                ", urlImagem=" + urlImagem + "]";
    }
}