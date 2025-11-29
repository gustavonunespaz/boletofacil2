package com.nunespaz.boletofacil2.domain.entity;

import com.nunespaz.boletofacil2.domain.valueobject.Endereco;

import java.util.Objects;

public class Cliente {
    private final String nome;
    private final Endereco endereco;

    public Cliente(String nome, Endereco endereco) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio");
        }
        this.nome = nome.trim();
        this.endereco = Objects.requireNonNull(endereco, "endereco");
    }

    public String getNome() {
        return nome;
    }

    public Endereco getEndereco() {
        return endereco;
    }
}
