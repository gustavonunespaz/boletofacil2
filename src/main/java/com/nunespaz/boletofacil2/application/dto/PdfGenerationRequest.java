package com.nunespaz.boletofacil2.application.dto;

import java.time.LocalDate;
import java.util.Objects;

public class PdfGenerationRequest {
    private final String caminhoOriginal;
    private final String nomeCliente;
    private final String enderecoFormatado;
    private final String vendaParcela;
    private final LocalDate dataVencimento;

    public PdfGenerationRequest(String caminhoOriginal, String nomeCliente, String enderecoFormatado, String vendaParcela, LocalDate dataVencimento) {
        if (caminhoOriginal == null || caminhoOriginal.isBlank()) {
            throw new IllegalArgumentException("Caminho do PDF original não pode ser vazio");
        }
        if (nomeCliente == null || nomeCliente.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio");
        }
        if (enderecoFormatado == null || enderecoFormatado.isBlank()) {
            throw new IllegalArgumentException("Endereço não pode ser vazio");
        }
        this.caminhoOriginal = caminhoOriginal;
        this.nomeCliente = nomeCliente.trim();
        this.enderecoFormatado = enderecoFormatado.trim();
        this.vendaParcela = vendaParcela == null ? "" : vendaParcela;
        this.dataVencimento = Objects.requireNonNull(dataVencimento, "dataVencimento");
    }

    public String getCaminhoOriginal() {
        return caminhoOriginal;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getEnderecoFormatado() {
        return enderecoFormatado;
    }

    public String getVendaParcela() {
        return vendaParcela;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }
}
