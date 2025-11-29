package com.nunespaz.boletofacil2.infrastructure.web;

import com.nunespaz.boletofacil2.application.dto.ProcessarBoletoResponse;
import com.nunespaz.boletofacil2.application.usecase.ListarBoletosUseCase;
import com.nunespaz.boletofacil2.application.usecase.ProcessarBoletoPdfUseCase;
import com.nunespaz.boletofacil2.domain.entity.Boleto;
import com.nunespaz.boletofacil2.domain.entity.Cliente;
import com.nunespaz.boletofacil2.domain.valueobject.Endereco;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoletoController.class)
class BoletoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessarBoletoPdfUseCase processarBoletoPdfUseCase;

    @MockBean
    private ListarBoletosUseCase listarBoletosUseCase;

    @Test
    void deveProcessarBoletoViaHttp() throws Exception {
        Boleto boleto = criarBoleto();
        when(processarBoletoPdfUseCase.executar(anyString())).thenReturn(new ProcessarBoletoResponse(boleto));

        mockMvc.perform(post("/api/boletos/processar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"caminhoPdf\":\"/tmp/boleto.pdf\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boleto.status", is("PROCESSADO")));
    }

    @Test
    void deveListarBoletosProcessados() throws Exception {
        Boleto boleto = criarBoleto();
        when(listarBoletosUseCase.executar()).thenReturn(List.of(boleto));

        mockMvc.perform(get("/api/boletos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cliente", is("Cliente Teste")))
                .andExpect(jsonPath("$[0].status", is("PROCESSADO")));
    }

    private Boleto criarBoleto() {
        Endereco endereco = new Endereco("Rua A", "100", "Casa", "Centro", "00000-000", "Cidade", "ST");
        Cliente cliente = new Cliente("Cliente Teste", endereco);
        Boleto boleto = new Boleto(UUID.randomUUID(), cliente, "1/1", LocalDate.now(), new BigDecimal("150.50"), "/tmp/original.pdf");
        boleto.marcarComoProcessado("/tmp/final.pdf");
        return boleto;
    }
}
