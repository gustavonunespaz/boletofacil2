package com.nunespaz.boletofacil2.application.usecase;

import com.nunespaz.boletofacil2.application.dto.PdfExtractionData;
import com.nunespaz.boletofacil2.application.dto.PdfGenerationRequest;
import com.nunespaz.boletofacil2.application.dto.ProcessarBoletoResponse;
import com.nunespaz.boletofacil2.application.port.BoletoRepository;
import com.nunespaz.boletofacil2.application.port.PdfService;
import com.nunespaz.boletofacil2.domain.entity.Boleto;
import com.nunespaz.boletofacil2.domain.valueobject.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessarBoletoPdfUseCaseTest {

    private PdfService pdfService;
    private BoletoRepository repository;
    private ProcessarBoletoPdfUseCase useCase;

    @BeforeEach
    void setUp() {
        pdfService = mock(PdfService.class);
        repository = mock(BoletoRepository.class);
        useCase = new ProcessarBoletoPdfUseCase(pdfService, repository, Clock.fixed(LocalDate.of(2024, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
    }

    @Test
    void deveProcessarBoletoEAtualizarStatus() {
        Endereco endereco = new Endereco("Rua A", "10", "", "Centro", "12345-000", "Cidade", "ST");
        PdfExtractionData extracao = new PdfExtractionData("Cliente Teste", endereco, "Venda 1/2", LocalDate.of(2024, 2, 1));

        when(pdfService.extrairDados("/tmp/original.pdf")).thenReturn(extracao);
        when(pdfService.gerarPdfFinal(any(PdfGenerationRequest.class))).thenReturn("Documentos/Boletos/2024/Fevereiro/Cliente Teste - 01-02-2024.pdf");
        when(repository.save(any(Boleto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProcessarBoletoResponse response = useCase.executar("/tmp/original.pdf");

        Boleto boleto = response.getBoleto();
        assertEquals(Boleto.Status.PROCESSADO, boleto.getStatus());
        assertEquals("Documentos/Boletos/2024/Fevereiro/Cliente Teste - 01-02-2024.pdf", boleto.getPdfProcessado());
        assertEquals("/tmp/original.pdf", boleto.getPdfOriginal());

        ArgumentCaptor<Boleto> captor = ArgumentCaptor.forClass(Boleto.class);
        verify(repository).save(captor.capture());
        assertEquals(boleto.getId(), captor.getValue().getId());
    }

    @Test
    void deveFalharQuandoPdfNaoInformado() {
        assertThrows(IllegalArgumentException.class, () -> useCase.executar(" "));
    }

    @Test
    void deveFalharQuandoExtracaoRetornaNulo() {
        when(pdfService.extrairDados("/tmp/original.pdf")).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> useCase.executar("/tmp/original.pdf"));
    }
}
