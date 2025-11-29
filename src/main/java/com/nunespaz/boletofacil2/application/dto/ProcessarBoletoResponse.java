package com.nunespaz.boletofacil2.application.dto;

import com.nunespaz.boletofacil2.domain.entity.Boleto;

public class ProcessarBoletoResponse {
    private final Boleto boleto;

    public ProcessarBoletoResponse(Boleto boleto) {
        this.boleto = boleto;
    }

    public Boleto getBoleto() {
        return boleto;
    }
}
