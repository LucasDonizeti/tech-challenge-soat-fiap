package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ListarServicosUseCase")
class ListarServicosUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private ListarServicosUseCase useCase;

    private Pageable pageable;
    private Page<Servico> servicoPage;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        Servico servico1 = Servico.criar("Troca de Óleo", "Troca de óleo sintético", new BigDecimal("150.00"));
        Servico servico2 = Servico.criar("Troca de Pneu", "Troca de pneu", new BigDecimal("200.00"));
        Servico servico3 = Servico.criar("Alinhamento", "Alinhamento de direção", new BigDecimal("80.00"));

        servicoPage = new PageImpl<>(List.of(servico1, servico2, servico3));
    }

    @Test
    @DisplayName("Deve listar serviços com paginação com sucesso")
    void deveListarServicosComPaginacaoComSucesso() {
        // Arrange
        when(repository.findAll(pageable)).thenReturn(servicoPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há serviços")
    void deveRetornarPaginaVaziaQuandoNaoHaServicos() {
        // Arrange
        Page<Servico> emptyPage = new PageImpl<>(List.of());
        when(repository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar serviços com página size diferente")
    void deveListarServicosComPaginaSizeDiferente() {
        // Arrange
        Pageable pageable5 = PageRequest.of(0, 5);
        when(repository.findAll(pageable5)).thenReturn(servicoPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable5);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(repository, times(1)).findAll(pageable5);
    }

    @Test
    @DisplayName("Deve listar serviços com página diferente")
    void deveListarServicosComPaginaDiferente() {
        // Arrange
        Pageable pageable1 = PageRequest.of(1, 10);
        Page<Servico> emptyPage = new PageImpl<>(List.of());
        when(repository.findAll(pageable1)).thenReturn(emptyPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable1);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(repository, times(1)).findAll(pageable1);
    }

    @Test
    @DisplayName("Deve mapear corretamente para ServicoResponse")
    void deveMapearCorretamenteParaServicoResponse() {
        // Arrange
        when(repository.findAll(pageable)).thenReturn(servicoPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Troca de Óleo", result.getContent().get(0).getNome());
        assertEquals("Troca de Pneu", result.getContent().get(1).getNome());
        assertEquals("Alinhamento", result.getContent().get(2).getNome());
    }

    @Test
    @DisplayName("Deve manter informações de paginação no response")
    void deveManterInformacoesDePaginacaoNoResponse() {
        // Arrange
        when(repository.findAll(pageable)).thenReturn(servicoPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getNumber());
        assertEquals(3, result.getSize());
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Deve listar apenas um serviço")
    void deveListarApenasUmServico() {
        // Arrange
        Servico servico = Servico.criar("Troca de Óleo", "Troca de óleo sintético", new BigDecimal("150.00"));
        Page<Servico> singlePage = new PageImpl<>(List.of(servico));
        when(repository.findAll(pageable)).thenReturn(singlePage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Troca de Óleo", result.getContent().get(0).getNome());
    }

    @Test
    @DisplayName("Deve manter preço nos responses")
    void deveManterPrecoNosResponses() {
        // Arrange
        when(repository.findAll(pageable)).thenReturn(servicoPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("150.00"), result.getContent().get(0).getPreco());
        assertEquals(new BigDecimal("200.00"), result.getContent().get(1).getPreco());
        assertEquals(new BigDecimal("80.00"), result.getContent().get(2).getPreco());
    }

    @Test
    @DisplayName("Deve manter status nos responses")
    void deveManterStatusNosResponses() {
        // Arrange
        when(repository.findAll(pageable)).thenReturn(servicoPage);

        // Act
        Page<ServicoResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().get(0).getAtivo());
        assertTrue(result.getContent().get(1).getAtivo());
        assertTrue(result.getContent().get(2).getAtivo());
    }
}
