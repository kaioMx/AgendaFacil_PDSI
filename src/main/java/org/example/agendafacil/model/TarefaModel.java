package org.example.agendafacil.model;

public class TarefaModel {

    private int idTarefa;
    private String titulo;
    private String descricao;
    private String data;
    private String horaInicio;
    private String horaFim;
    private String horaAlarme;
    private String status;
    private byte [] somNotificacao;
    private String corCategoria;
    public TarefaModel(){

    }

    public int getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(int idTarefa) {
        this.idTarefa = idTarefa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getStatus() {
        return status;
    }

    public String getHoraAlarme() {
        return horaAlarme;
    }

    public void setHoraAlarme(String horaAlarme) {
        this.horaAlarme = horaAlarme;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getSomNotificacao() {
        return somNotificacao;
    }

    public void setSomNotificacao(byte[] somNotificacao) {
        this.somNotificacao = somNotificacao;
    }
    public String getCorCategoria() {
        return corCategoria;
    }

    public void setCorCategoria(String corCategoria) {
        this.corCategoria = corCategoria;
    }
}
