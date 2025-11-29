package com.nunespaz.boletofacil2.infrastructure.persistence;

import com.nunespaz.boletofacil2.application.port.BoletoRepository;
import com.nunespaz.boletofacil2.domain.entity.Boleto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryBoletoRepository implements BoletoRepository {

    private final Map<UUID, Boleto> storage = new HashMap<>();

    @Override
    public synchronized Boleto save(Boleto boleto) {
        storage.put(boleto.getId(), boleto);
        return boleto;
    }

    @Override
    public Map<UUID, Boleto> findAll() {
        return Collections.unmodifiableMap(storage);
    }
}
