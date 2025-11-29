package com.nunespaz.boletofacil2.application.usecase;

import com.nunespaz.boletofacil2.application.port.BoletoRepository;
import com.nunespaz.boletofacil2.domain.entity.Boleto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ListarBoletosUseCase {

    private final BoletoRepository boletoRepository;

    public ListarBoletosUseCase(BoletoRepository boletoRepository) {
        this.boletoRepository = Objects.requireNonNull(boletoRepository, "boletoRepository");
    }

    public List<Boleto> executar() {
        List<Boleto> boletos = new ArrayList<>();
        boletoRepository.findAll().forEach(boletos::add);
        return boletos.stream()
                .sorted(Comparator.comparing(boleto -> boleto.getCliente().getNome(), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
