package com.techchallenge.oficina.sharedkernel.application.usecases;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories.ClienteJpaRepository;
import com.techchallenge.oficina.sharedkernel.application.usecases.commands.CadastrarSenhaClienteCommand;
import com.techchallenge.oficina.sharedkernel.application.usecases.ports.input.CadastrarSenhaClienteInput;
import com.techchallenge.oficina.sharedkernel.domain.exceptions.ClienteNaoEncontradoException;
import com.techchallenge.oficina.sharedkernel.domain.exceptions.ValorInvalidoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastrarSenhaClienteUseCase implements CadastrarSenhaClienteInput {

    private final ClienteJpaRepository clienteJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(CadastrarSenhaClienteCommand command) {
        String documentoLimpo = normalizeDocumento(command.getDocumento());
        validarDocumento(documentoLimpo);

        var clienteOpt = clienteJpaRepository.findByCpf(documentoLimpo)
                .or(() -> clienteJpaRepository.findByCnpj(documentoLimpo));

        if (clienteOpt.isEmpty() || clienteOpt.get().getStatus() == null ||
                !"ATIVO".equals(clienteOpt.get().getStatus().name())) {
            throw new ClienteNaoEncontradoException("Documento não encontrado ou cliente inativo");
        }

        var cliente = clienteOpt.get();
        cliente.setSenhaHash(passwordEncoder.encode(command.getSenha()));
        clienteJpaRepository.save(cliente);
    }

    private String normalizeDocumento(String documento) {
        if (documento == null) {
            return null;
        }
        return documento.replaceAll("\\D", "");
    }

    private void validarDocumento(String documentoLimpo) {
        if (documentoLimpo == null || documentoLimpo.isBlank()) {
            throw new ValorInvalidoException("Documento inválido");
        }

        if (documentoLimpo.length() == 11) {
            CPF.of(documentoLimpo);
            return;
        }

        if (documentoLimpo.length() == 14) {
            CNPJ.of(documentoLimpo);
            return;
        }

        throw new ValorInvalidoException("Documento inválido");
    }
}
