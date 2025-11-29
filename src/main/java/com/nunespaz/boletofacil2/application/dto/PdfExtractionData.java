package com.nunespaz.boletofacil2.application.dto;

import com.nunespaz.boletofacil2.domain.valueobject.Endereco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class PdfExtractionData {
    private final String nomeCliente;
    private final Endereco endereco;
    private final String vendaParcela;
    private final LocalDate dataVencimento;
    private final BigDecimal valorBoleto;
    private final String enderecoFormatadoOriginal;

    public PdfExtractionData(String nomeCliente, Endereco endereco, String vendaParcela, LocalDate dataVencimento, BigDecimal valorBoleto, String enderecoFormatadoOriginal) {
        if (nomeCliente == null || nomeCliente.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio");
        }
        this.nomeCliente = nomeCliente.trim();
        this.endereco = Objects.requireNonNull(endereco, "endereco");
        this.vendaParcela = vendaParcela == null ? "" : vendaParcela;
        this.dataVencimento = Objects.requireNonNull(dataVencimento, "dataVencimento");
        this.valorBoleto = valorBoleto == null ? BigDecimal.ZERO : valorBoleto;
        if (enderecoFormatadoOriginal == null || enderecoFormatadoOriginal.isBlank()) {
            throw new IllegalArgumentException("Endereço formatado original não pode ser vazio");
        }
        this.enderecoFormatadoOriginal = enderecoFormatadoOriginal.trim();
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public String getVendaParcela() {
        return vendaParcela;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public BigDecimal getValorBoleto() {
        return valorBoleto;
    }

    public String getEnderecoFormatadoOriginal() {
        return enderecoFormatadoOriginal;
    }
}
