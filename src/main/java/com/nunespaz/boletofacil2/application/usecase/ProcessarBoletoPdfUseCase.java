package com.nunespaz.boletofacil2.application.usecase;

import com.nunespaz.boletofacil2.application.dto.PdfExtractionData;
import com.nunespaz.boletofacil2.application.dto.PdfGenerationRequest;
import com.nunespaz.boletofacil2.application.dto.ProcessarBoletoResponse;
import com.nunespaz.boletofacil2.application.port.BoletoRepository;
import com.nunespaz.boletofacil2.application.port.PdfService;
import com.nunespaz.boletofacil2.domain.entity.Boleto;
import com.nunespaz.boletofacil2.domain.entity.Cliente;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

public class ProcessarBoletoPdfUseCase {
    private final PdfService pdfService;
    private final BoletoRepository boletoRepository;
    private final Clock clock;

    public ProcessarBoletoPdfUseCase(PdfService pdfService, BoletoRepository boletoRepository, Clock clock) {
        this.pdfService = Objects.requireNonNull(pdfService, "pdfService");
        this.boletoRepository = Objects.requireNonNull(boletoRepository, "boletoRepository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ProcessarBoletoResponse executar(String caminhoPdf) {
        if (caminhoPdf == null || caminhoPdf.isBlank()) {
            throw new IllegalArgumentException("Caminho do PDF não pode ser vazio");
        }

        PdfExtractionData dadosExtraidos = pdfService.extrairDados(caminhoPdf);
        if (dadosExtraidos == null) {
            throw new IllegalStateException("Não foi possível extrair dados do PDF");
        }

        Cliente cliente = new Cliente(dadosExtraidos.getNomeCliente(), dadosExtraidos.getEndereco());
        Boleto boleto = new Boleto(UUID.randomUUID(), cliente, dadosExtraidos.getVendaParcela(), dadosExtraidos.getDataVencimento(), caminhoPdf);

        PdfGenerationRequest request = new PdfGenerationRequest(
                caminhoPdf,
                dadosExtraidos.getNomeCliente(),
                dadosExtraidos.getEndereco().formatadoEmLinhas(),
                dadosExtraidos.getVendaParcela(),
                dadosExtraidos.getDataVencimento()
        );

        String caminhoProcessado = pdfService.gerarPdfFinal(request);
        boleto.marcarComoProcessado(caminhoProcessado);
        boletoRepository.save(boleto);

        return new ProcessarBoletoResponse(boleto);
    }
}
