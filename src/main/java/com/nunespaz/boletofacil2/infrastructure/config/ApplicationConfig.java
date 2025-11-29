package com.nunespaz.boletofacil2.infrastructure.config;

import com.nunespaz.boletofacil2.application.port.BoletoRepository;
import com.nunespaz.boletofacil2.application.port.PdfService;
import com.nunespaz.boletofacil2.application.usecase.ListarBoletosUseCase;
import com.nunespaz.boletofacil2.application.usecase.ProcessarBoletoPdfUseCase;
import com.nunespaz.boletofacil2.infrastructure.pdf.PdfBoxPdfService;
import com.nunespaz.boletofacil2.infrastructure.persistence.InMemoryBoletoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ApplicationConfig {

    @Bean
    public PdfService pdfService() {
        return new PdfBoxPdfService();
    }

    @Bean
    public BoletoRepository boletoRepository() {
        return new InMemoryBoletoRepository();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ProcessarBoletoPdfUseCase processarBoletoPdfUseCase(PdfService pdfService, BoletoRepository boletoRepository, Clock clock) {
        return new ProcessarBoletoPdfUseCase(pdfService, boletoRepository, clock);
    }

    @Bean
    public ListarBoletosUseCase listarBoletosUseCase(BoletoRepository boletoRepository) {
        return new ListarBoletosUseCase(boletoRepository);
    }
}
