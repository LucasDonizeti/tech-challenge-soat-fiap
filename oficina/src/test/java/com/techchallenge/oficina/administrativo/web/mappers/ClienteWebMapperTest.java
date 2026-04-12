package com.techchallenge.oficina.administrativo.web.mappers;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.web.dto.ClienteResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ClienteWebMapper")
class ClienteWebMapperTest {

    @InjectMocks
    private ClienteWebMapper mapper;

    private ClienteResponse clienteResponse;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        
        clienteResponse = ClienteResponse.builder()
                .id(id)
                .nome("João Silva")
                .cpf("12345678901")
                .cnpj(null)
                .email("joao@example.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(2)
                .build();
    }

    @Test
    @DisplayName("Deve converter ClienteResponse para ClienteResponseDto")
    void deveConverterClienteResponseParaDto() {
        // Act
        ClienteResponseDto result = mapper.toDto(clienteResponse);

        // Assert
        assertNotNull(result);
        assertEquals(clienteResponse.getId(), result.getId());
        assertEquals(clienteResponse.getNome(), result.getNome());
        assertEquals(clienteResponse.getCpf(), result.getCpf());
        assertEquals(clienteResponse.getEmail(), result.getEmail());
        assertEquals(clienteResponse.getStatus(), result.getStatus());
        assertEquals(clienteResponse.isPessoaFisica(), result.isPessoaFisica());
        assertEquals(clienteResponse.isPessoaJuridica(), result.isPessoaJuridica());
        assertEquals(clienteResponse.getQuantidadeVeiculos(), result.getQuantidadeVeiculos());
    }

    @Test
    @DisplayName("Deve converter ClienteResponse nulo para null")
    void deveConverterClienteResponseNuloParaNull() {
        // Act
        ClienteResponseDto result = mapper.toDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Deve converter lista de ClienteResponse para lista de ClienteResponseDto")
    void deveConverterListaDeClienteResponseParaDto() {
        // Arrange
        List<ClienteResponse> responses = List.of(clienteResponse);

        // Act
        List<ClienteResponseDto> result = mapper.toDtoList(responses);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(clienteResponse.getId(), result.get(0).getId());
        assertEquals(clienteResponse.getNome(), result.get(0).getNome());
    }

    @Test
    @DisplayName("Deve converter lista vazia de ClienteResponse para lista vazia")
    void deveConverterListaVaziaParaListaVazia() {
        // Arrange
        List<ClienteResponse> responses = List.of();

        // Act
        List<ClienteResponseDto> result = mapper.toDtoList(responses);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve converter lista nula de ClienteResponse para lista vazia")
    void deveConverterListaNulaParaListaVazia() {
        // Act
        List<ClienteResponseDto> result = mapper.toDtoList(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve converter lista com múltiplos ClienteResponse")
    void deveConverterListaComMultiplosClienteResponse() {
        // Arrange
        UUID id2 = UUID.randomUUID();
        ClienteResponse response2 = ClienteResponse.builder()
                .id(id2)
                .nome("Maria Santos")
                .cpf("98765432100")
                .cnpj(null)
                .email("maria@example.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(1)
                .build();

        List<ClienteResponse> responses = List.of(clienteResponse, response2);

        // Act
        List<ClienteResponseDto> result = mapper.toDtoList(responses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(clienteResponse.getId(), result.get(0).getId());
        assertEquals(id2, result.get(1).getId());
    }

    @Test
    @DisplayName("Deve converter página de ClienteResponse para página de ClienteResponseDto")
    void deveConverterPaginaDeClienteResponseParaDto() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ClienteResponse> responsePage = new PageImpl<>(List.of(clienteResponse), pageRequest, 1);

        // Act
        Page<ClienteResponseDto> result = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(clienteResponse.getId(), result.getContent().get(0).getId());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
    }

    @Test
    @DisplayName("Deve converter página vazia de ClienteResponse para página vazia")
    void deveConverterPaginaVaziaParaPaginaVazia() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ClienteResponse> responsePage = new PageImpl<>(List.of(), pageRequest, 0);

        // Act
        Page<ClienteResponseDto> result = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Deve converter página nula de ClienteResponse para página vazia")
    void deveConverterPaginaNulaParaPaginaVazia() {
        // Act
        Page<ClienteResponseDto> result = mapper.toDtoPage(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve converter página com múltiplos ClienteResponse")
    void deveConverterPaginaComMultiplosClienteResponse() {
        // Arrange
        UUID id2 = UUID.randomUUID();
        ClienteResponse response2 = ClienteResponse.builder()
                .id(id2)
                .nome("Maria Santos")
                .cpf("98765432100")
                .cnpj(null)
                .email("maria@example.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(1)
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ClienteResponse> responsePage = new PageImpl<>(List.of(clienteResponse, response2), pageRequest, 2);

        // Act
        Page<ClienteResponseDto> result = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(clienteResponse.getId(), result.getContent().get(0).getId());
        assertEquals(id2, result.getContent().get(1).getId());
    }

    @Test
    @DisplayName("Deve manter paginação ao converter página")
    void deveManterPaginacaoAoConverterPagina() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(1, 5);
        Page<ClienteResponse> responsePage = new PageImpl<>(List.of(clienteResponse), pageRequest, 10);

        // Act
        Page<ClienteResponseDto> result = mapper.toDtoPage(responsePage);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(10, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve converter ClienteResponse de pessoa jurídica")
    void deveConverterClienteResponsePessoaJuridica() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse pjResponse = ClienteResponse.builder()
                .id(id)
                .nome("Empresa LTDA")
                .cpf(null)
                .cnpj("12345678000190")
                .email("contato@empresa.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .isPessoaFisica(false)
                .isPessoaJuridica(true)
                .quantidadeVeiculos(5)
                .build();

        // Act
        ClienteResponseDto result = mapper.toDto(pjResponse);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Empresa LTDA", result.getNome());
        assertEquals("12345678000190", result.getCnpj());
        assertFalse(result.isPessoaFisica());
        assertTrue(result.isPessoaJuridica());
        assertEquals(5, result.getQuantidadeVeiculos());
    }
}
