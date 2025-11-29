package com.nunespaz.boletofacil2.application.dto;

import com.nunespaz.boletofacil2.domain.entity.Boleto;

import java.time.LocalDate;
import java.util.UUID;

public class BoletoResumoResponse {

    private final UUID id;
    private final String cliente;
    private final String vendaParcela;
    private final LocalDate vencimento;
    private final String status;
    private final String pdfProcessado;

    public BoletoResumoResponse(Boleto boleto) {
        this.id = boleto.getId();
        this.cliente = boleto.getCliente().getNome();
        this.vendaParcela = boleto.getVendaParcela();
        this.vencimento = boleto.getDataVencimento();
        this.status = boleto.getStatus().name();
        this.pdfProcessado = boleto.getPdfProcessado();
    }

    public UUID getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getVendaParcela() {
        return vendaParcela;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public String getStatus() {
        return status;
    }

    public String getPdfProcessado() {
        return pdfProcessado;
    }
}
