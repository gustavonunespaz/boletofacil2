package com.nunespaz.boletofacil2.application.port;

import com.nunespaz.boletofacil2.domain.entity.Boleto;

public interface BoletoRepository {
    Boleto save(Boleto boleto);

    Iterable<Boleto> findAll();
}
