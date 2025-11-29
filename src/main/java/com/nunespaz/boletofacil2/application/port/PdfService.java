package com.nunespaz.boletofacil2.application.port;

import com.nunespaz.boletofacil2.application.dto.PdfExtractionData;
import com.nunespaz.boletofacil2.application.dto.PdfGenerationRequest;

public interface PdfService {
    PdfExtractionData extrairDados(String caminhoPdf);

    String gerarPdfFinal(PdfGenerationRequest request);
}
