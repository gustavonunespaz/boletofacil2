package com.nunespaz.boletofacil2.domain.entity;

import com.nunespaz.boletofacil2.domain.valueobject.Endereco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Boleto {
    private final UUID id;
    private final Cliente cliente;
    private final String vendaParcela;
    private final LocalDate dataVencimento;
    private final BigDecimal valor;
    private final String pdfOriginal;
    private String pdfProcessado;
    private Status status;

    public Boleto(UUID id, Cliente cliente, String vendaParcela, LocalDate dataVencimento, BigDecimal valor, String pdfOriginal) {
        this.id = Objects.requireNonNull(id, "id");
        this.cliente = Objects.requireNonNull(cliente, "cliente");
        this.vendaParcela = vendaParcela == null ? "" : vendaParcela;
        this.dataVencimento = Objects.requireNonNull(dataVencimento, "dataVencimento");
        this.valor = valor == null ? BigDecimal.ZERO : valor;
        this.pdfOriginal = Objects.requireNonNull(pdfOriginal, "pdfOriginal");
        this.status = Status.NOVO;
    }

    public UUID getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getVendaParcela() {
        return vendaParcela;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getPdfOriginal() {
        return pdfOriginal;
    }

    public String getPdfProcessado() {
        return pdfProcessado;
    }

    public Status getStatus() {
        return status;
    }

    public void marcarComoProcessado(String caminhoProcessado) {
        if (caminhoProcessado == null || caminhoProcessado.isBlank()) {
            throw new IllegalArgumentException("Caminho do PDF processado não pode ser vazio");
        }
        this.pdfProcessado = caminhoProcessado;
        this.status = Status.PROCESSADO;
    }

    public Endereco getEndereco() {
        return cliente.getEndereco();
    }

    public enum Status {
        NOVO, PROCESSADO, ENVIADO, CANCELADO
    }
}
