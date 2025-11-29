package com.nunespaz.boletofacil2.infrastructure.web;

import com.nunespaz.boletofacil2.application.dto.BoletoResumoResponse;
import com.nunespaz.boletofacil2.application.dto.ProcessarBoletoRequest;
import com.nunespaz.boletofacil2.application.dto.ProcessarBoletoResponse;
import com.nunespaz.boletofacil2.application.usecase.ListarBoletosUseCase;
import com.nunespaz.boletofacil2.application.usecase.ProcessarBoletoPdfUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

    @PostMapping(value = "/processar-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ProcessarBoletoResponse>> processarUpload(@RequestParam("arquivos") MultipartFile[] arquivos) {
        if (arquivos == null || arquivos.length == 0) {
            return ResponseEntity.badRequest().build();
        }

        List<ProcessarBoletoResponse> respostas = new ArrayList<>();

        for (MultipartFile arquivo : arquivos) {
            if (arquivo == null || arquivo.isEmpty()) {
                continue;
            }

            Path destinoTemporario = null;
            try {
                destinoTemporario = Files.createTempFile("boleto-upload-", ".pdf");
                arquivo.transferTo(destinoTemporario.toFile());
                ProcessarBoletoResponse resposta = processarBoletoPdfUseCase.executar(destinoTemporario.toString());
                respostas.add(resposta);
            } catch (IOException e) {
                throw new IllegalStateException("Falha ao armazenar o PDF enviado", e);
            } finally {
                if (destinoTemporario != null) {
                    try {
                        Files.deleteIfExists(destinoTemporario);
                    } catch (IOException ignored) {
                        // Arquivo temporário pode ser removido manualmente posteriormente
                    }
                }
            }
        }

        if (respostas.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(respostas);
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
