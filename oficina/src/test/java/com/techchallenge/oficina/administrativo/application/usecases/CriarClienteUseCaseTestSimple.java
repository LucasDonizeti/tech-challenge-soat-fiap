package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.CpfJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.services.ClienteDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarClienteUseCaseTestSimple {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteDomainService domainService;

    @InjectMocks
    private CriarClienteUseCase criarClienteUseCase;

    private CriarClienteCommand command;

    @BeforeEach
    void setUp() {
        command = new CriarClienteCommand(
                "João Silva",
                "12345678909",
                null,
                "joao.silva@email.com"
        );
    }

    @Test
    void deveCriarClientePessoaFisicaComSucesso() {
        // Arrange
        Cliente clienteSalvo = Cliente.criar(
                Nome.of("João Silva"),
                CPF.of("12345678909"),
                Email.of("joao.silva@email.com")
        );
        
        when(repository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        ClienteResponse response = criarClienteUseCase.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals("João Silva", response.getNome());
        assertEquals("123.456.789-09", response.getCpf());
        assertNull(response.getCnpj());
        assertEquals("joao.silva@email.com", response.getEmail());
        assertTrue(response.isPessoaFisica());
        assertFalse(response.isPessoaJuridica());

        // Verificar se validações foram chamadas
        verify(domainService).validarIdentificadoresUnicos(any(CPF.class), isNull(), any(Email.class));
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void deveCriarClientePessoaJuridicaComSucesso() {
        // Arrange
        CriarClienteCommand commandPJ = new CriarClienteCommand(
                "Auto Peças Ltda",
                null,
                "12345678000195",
                "contato@autopecas.com.br"
        );
        
        Cliente clienteSalvo = Cliente.criarPJ(
                Nome.of("Auto Peças Ltda"),
                CNPJ.of("12345678000195"),
                Email.of("contato@autopecas.com.br")
        );
        
        when(repository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        ClienteResponse response = criarClienteUseCase.execute(commandPJ);

        // Assert
        assertNotNull(response);
        assertEquals("Auto Peças Ltda", response.getNome());
        assertNull(response.getCpf());
        assertEquals("12.345.678/0001-95", response.getCnpj());
        assertEquals("contato@autopecas.com.br", response.getEmail());
        assertFalse(response.isPessoaFisica());
        assertTrue(response.isPessoaJuridica());

        // Verificar se validações foram chamadas
        verify(domainService).validarIdentificadoresUnicos(isNull(), any(CNPJ.class), any(Email.class));
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // Arrange
        doThrow(new CpfJaCadastradoException(CPF.of("12345678909")))
                .when(domainService).validarIdentificadoresUnicos(any(CPF.class), isNull(), any(Email.class));

        // Act & Assert
        CpfJaCadastradoException exception = assertThrows(
                CpfJaCadastradoException.class,
                () -> criarClienteUseCase.execute(command)
        );

        assertEquals("CPF já cadastrado: 123.456.789-09", exception.getMessage());

        // Verificar que não tentou salvar
        verify(repository, never()).save(any(Cliente.class));
    }
}
