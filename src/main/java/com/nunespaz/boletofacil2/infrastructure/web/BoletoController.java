package com.nunespaz.boletofacil2.infrastructure.web;

import com.nunespaz.boletofacil2.application.dto.BoletoResumoResponse;
import com.nunespaz.boletofacil2.application.dto.ProcessarBoletoRequest;
import com.nunespaz.boletofacil2.application.dto.ProcessarBoletoResponse;
import com.nunespaz.boletofacil2.application.usecase.ListarBoletosUseCase;
import com.nunespaz.boletofacil2.application.usecase.ProcessarBoletoPdfUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boletos")
public class BoletoController {

    private final ProcessarBoletoPdfUseCase processarBoletoPdfUseCase;
    private final ListarBoletosUseCase listarBoletosUseCase;

    public BoletoController(ProcessarBoletoPdfUseCase processarBoletoPdfUseCase, ListarBoletosUseCase listarBoletosUseCase) {
        this.processarBoletoPdfUseCase = processarBoletoPdfUseCase;
        this.listarBoletosUseCase = listarBoletosUseCase;
    }

    @PostMapping("/processar")
    public ResponseEntity<ProcessarBoletoResponse> processar(@RequestBody ProcessarBoletoRequest request) {
        if (request == null || request.getCaminhoPdf() == null || request.getCaminhoPdf().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ProcessarBoletoResponse response = processarBoletoPdfUseCase.executar(request.getCaminhoPdf());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BoletoResumoResponse>> listar() {
        List<BoletoResumoResponse> boletos = listarBoletosUseCase.executar()
                .stream()
                .map(BoletoResumoResponse::new)
                .toList();
        return ResponseEntity.ok(boletos);
    }
}
