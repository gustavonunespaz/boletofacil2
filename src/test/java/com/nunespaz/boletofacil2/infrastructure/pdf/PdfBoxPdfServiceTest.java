package com.nunespaz.boletofacil2.infrastructure.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nunespaz.boletofacil2.application.dto.PdfExtractionData;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

class PdfBoxPdfServiceTest {

    private final PdfBoxPdfService pdfService = new PdfBoxPdfService();

    @Test
    void deveExtrairValorProximoDoNossoNumeroQuandoExistiremOutrosValores() throws Exception {
        Path pdfTemporario = Files.createTempFile("boleto-contexto-", ".pdf");
        try {
            criarPdfDeTeste(pdfTemporario);

            PdfExtractionData dados = pdfService.extrairDados(pdfTemporario.toString());

            assertNotNull(dados);
            assertEquals(new BigDecimal("139.02"), dados.getValorBoleto());
            assertEquals("Cliente Exemplo", dados.getNomeCliente());
            assertEquals(LocalDate.of(2025, 12, 18), dados.getDataVencimento());
        } finally {
            Files.deleteIfExists(pdfTemporario);
        }
    }

    private void criarPdfDeTeste(Path destino) throws IOException {
        List<String> linhas = List.of(
                "Resumo de multa R$ 10,00",
                "Sacado/Cliente",
                "Cliente Exemplo",
                "Rua Teste, 100 - Bairro Centro",
                "Instruções",
                "Venda 1/1",
                "Vencimento",
                "18/12/2025",
                "R$ 139,02",
                "Nosso número",
                "11509252334572"
        );

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(16);
                contentStream.newLineAtOffset(50, 700);

                for (String linha : linhas) {
                    contentStream.showText(linha);
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(destino.toFile());
        }
    }
}
