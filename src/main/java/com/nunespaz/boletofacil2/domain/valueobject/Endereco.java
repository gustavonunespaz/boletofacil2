package com.nunespaz.boletofacil2.domain.valueobject;

import java.util.Objects;

public class Endereco {
    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cep;
    private final String cidade;
    private final String estado;

    public Endereco(String logradouro, String numero, String complemento, String bairro, String cep, String cidade, String estado) {
        this.logradouro = requireNonBlank(logradouro, "logradouro");
        this.numero = requireNonBlank(numero, "numero");
        this.complemento = complemento == null ? "" : complemento;
        this.bairro = requireNonBlank(bairro, "bairro");
        this.cep = requireNonBlank(cep, "cep");
        this.cidade = requireNonBlank(cidade, "cidade");
        this.estado = requireNonBlank(estado, "estado");
    }

    private String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Endereco " + field + " não pode ser vazio");
        }
        return value.trim();
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCep() {
        return cep;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String formatadoEmLinhas() {
        String linhaEndereco = logradouro + ", " + numero + (complemento.isBlank() ? "" : " - " + complemento);
        String linhaCidade = bairro + " " + cep + " - " + cidade + "/" + estado;
        return linhaEndereco + "\n" + linhaCidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(logradouro, endereco.logradouro) && Objects.equals(numero, endereco.numero) && Objects.equals(complemento, endereco.complemento) && Objects.equals(bairro, endereco.bairro) && Objects.equals(cep, endereco.cep) && Objects.equals(cidade, endereco.cidade) && Objects.equals(estado, endereco.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logradouro, numero, complemento, bairro, cep, cidade, estado);
    }
}
