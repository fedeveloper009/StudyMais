package com.projeto.studymais.service;

import com.projeto.studymais.exception.LimitePlataformasException;
import com.projeto.studymais.model.Plano;
import org.springframework.stereotype.Service;

@Service
public class LimitePlanoService {

    public void validarCriacao(Plano plano, long quantidadeAtual) {
        Plano planoSeguro = plano == null ? Plano.FREE : plano;
        int limite = limiteDe(planoSeguro);
        if (limite != Integer.MAX_VALUE && quantidadeAtual >= limite) {
            throw new LimitePlataformasException(
                    "Limite de plataformas atingido. Seu plano "
                            + planoSeguro + " permite no maximo " + limite + " plataformas."
            );
        }
    }

    public int limiteDe(Plano plano) {
        Plano planoSeguro = plano == null ? Plano.FREE : plano;
        return switch (planoSeguro) {
            case FREE -> 5;
            case PRO -> 10;
            case PREMIUM -> Integer.MAX_VALUE;
        };
    }
}
