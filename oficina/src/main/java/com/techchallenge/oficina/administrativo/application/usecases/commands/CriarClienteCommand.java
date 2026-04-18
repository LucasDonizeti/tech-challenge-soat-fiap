package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import lombok.Getter;

@Getter
public class CriarClienteCommand {
    
    private final Nome nome;
    private final CPF cpf;
    private final CNPJ cnpj;
    private final Email email;
    
    public CriarClienteCommand(String nome, String cpf, String cnpj, String email) {
        validate(nome, cpf, cnpj, email);
        
        this.nome = Nome.of(nome);
        this.cpf = cpf != null ? CPF.of(cpf) : null;
        this.cnpj = cnpj != null ? CNPJ.of(cnpj) : null;
        this.email = Email.of(email);
        
        validateTipoPessoa();
    }
    
    private void validate(String nome, String cpf, String cnpj, String email) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoClienteException("Nome é obrigatório");
        }
        if (email == null || email.isBlank()) {
            throw new ValidacaoClienteException("Email é obrigatório");
        }
        if ((cpf == null || cpf.isBlank()) && (cnpj == null || cnpj.isBlank())) {
            throw new ValidacaoClienteException("CPF ou CNPJ é obrigatório");
        }
        if (cpf != null && !cpf.isBlank() && cnpj != null && !cnpj.isBlank()) {
            throw new ValidacaoClienteException("Cliente não pode ter CPF e CNPJ simultaneamente");
        }
    }
    
    private void validateTipoPessoa() {
        if (cpf != null && cnpj != null) {
            throw new ValidacaoClienteException("Cliente deve ser Pessoa Física (CPF) ou Pessoa Jurídica (CNPJ), não ambos");
        }
        if (cpf == null && cnpj == null) {
            throw new ValidacaoClienteException("Cliente deve ter CPF ou CNPJ");
        }
    }
    
    public boolean isPessoaFisica() {
        return cpf != null;
    }
    
    public boolean isPessoaJuridica() {
        return cnpj != null;
    }
}
