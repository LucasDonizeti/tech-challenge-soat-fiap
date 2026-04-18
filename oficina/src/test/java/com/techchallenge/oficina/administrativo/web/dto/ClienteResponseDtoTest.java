package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ClienteResponseDto")
class ClienteResponseDtoTest {

    @Test
    @DisplayName("Deve criar ClienteResponseDto usando setters")
    void deveCriarClienteResponseDtoUsandoSetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now();
        LocalDateTime atualizadoEm = LocalDateTime.now();

        // Act
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(id);
        dto.setNome("João Silva");
        dto.setCpf("12345678909");
        dto.setCnpj("12345678000190");
        dto.setEmail("joao.silva@exemplo.com");
        dto.setStatus(StatusCliente.ATIVO);
        dto.setCriadoEm(criadoEm);
        dto.setAtualizadoEm(atualizadoEm);
        dto.setPessoaFisica(true);
        dto.setPessoaJuridica(false);
        dto.setQuantidadeVeiculos(2);
        dto.setTipoPessoa("PF");
        dto.setDocumento("12345678909");

        // Assert
        assertEquals(id, dto.getId());
        assertEquals("João Silva", dto.getNome());
        assertEquals("12345678909", dto.getCpf());
        assertEquals("12345678000190", dto.getCnpj());
        assertEquals("joao.silva@exemplo.com", dto.getEmail());
        assertEquals(StatusCliente.ATIVO, dto.getStatus());
        assertEquals(criadoEm, dto.getCriadoEm());
        assertEquals(atualizadoEm, dto.getAtualizadoEm());
        assertTrue(dto.isPessoaFisica());
        assertFalse(dto.isPessoaJuridica());
        assertEquals(2, dto.getQuantidadeVeiculos());
        assertEquals("PF", dto.getTipoPessoa());
        assertEquals("12345678909", dto.getDocumento());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto a partir de ClienteResponse")
    void deveCriarClienteResponseDtoAPartirDeClienteResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.now();
        LocalDateTime atualizadoEm = LocalDateTime.now();

        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Maria Santos")
                .cpf("98765432100")
                .email("maria.santos@exemplo.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .isPessoaFisica(false)
                .isPessoaJuridica(true)
                .quantidadeVeiculos(5)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Maria Santos", dto.getNome());
        assertEquals("98765432100", dto.getCpf());
        assertEquals("maria.santos@exemplo.com", dto.getEmail());
        assertEquals(StatusCliente.ATIVO, dto.getStatus());
        assertEquals(criadoEm, dto.getCriadoEm());
        assertEquals(atualizadoEm, dto.getAtualizadoEm());
        assertFalse(dto.isPessoaFisica());
        assertTrue(dto.isPessoaJuridica());
        assertEquals(5, dto.getQuantidadeVeiculos());
        assertEquals("Pessoa Jurídica", dto.getTipoPessoa());
        assertEquals("98765432100", dto.getDocumento());
    }

    @Test
    @DisplayName("Deve retornar null quando ClienteResponse é nulo")
    void deveRetornarNullQuandoClienteResponseENulo() {
        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(null);

        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto para pessoa física")
    void deveCriarClienteResponseDtoParaPessoaFisica() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Carlos Alberto")
                .cpf("11122233344")
                .email("carlos.alberto@exemplo.com")
                .status(StatusCliente.ATIVO)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertTrue(dto.isPessoaFisica());
        assertFalse(dto.isPessoaJuridica());
        assertEquals("Pessoa Física", dto.getTipoPessoa());
        assertEquals("11122233344", dto.getDocumento());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto para pessoa jurídica")
    void deveCriarClienteResponseDtoParaPessoaJuridica() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Empresa XYZ Ltda")
                .cnpj("11444777000161")
                .email("contato@xyz.com")
                .status(StatusCliente.ATIVO)
                .isPessoaFisica(false)
                .isPessoaJuridica(true)
                .quantidadeVeiculos(10)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertFalse(dto.isPessoaFisica());
        assertTrue(dto.isPessoaJuridica());
        assertEquals("Pessoa Jurídica", dto.getTipoPessoa());
        assertEquals("11444777000161", dto.getDocumento());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto com status INATIVO")
    void deveCriarClienteResponseDtoComStatusInativo() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Pedro Oliveira")
                .cpf("55566677788")
                .email("pedro.oliveira@exemplo.com")
                .status(StatusCliente.INATIVO)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(1)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(StatusCliente.INATIVO, dto.getStatus());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto com zero veículos")
    void deveCriarClienteResponseDtoComZeroVeiculos() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Ana Paula")
                .cpf("99988877766")
                .email("ana.paula@exemplo.com")
                .status(StatusCliente.ATIVO)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(0, dto.getQuantidadeVeiculos());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto com múltiplos veículos")
    void deveCriarClienteResponseDtoComMultiplosVeiculos() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Transportadora Rápido")
                .cnpj("11444777000161")
                .email("contato@rapido.com")
                .status(StatusCliente.ATIVO)
                .isPessoaFisica(false)
                .isPessoaJuridica(true)
                .quantidadeVeiculos(25)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(25, dto.getQuantidadeVeiculos());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto com timestamps")
    void deveCriarClienteResponseDtoComTimestamps() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime criadoEm = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime atualizadoEm = LocalDateTime.of(2024, 1, 15, 14, 30);

        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Fernando Costa")
                .cpf("44455566677")
                .email("fernando.costa@exemplo.com")
                .status(StatusCliente.ATIVO)
                .criadoEm(criadoEm)
                .atualizadoEm(atualizadoEm)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(3)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertEquals(criadoEm, dto.getCriadoEm());
        assertEquals(atualizadoEm, dto.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve permitir alteração de todos os campos via setters")
    void devePermitirAlteracaoDeTodosOsCamposViaSetters() {
        // Arrange
        ClienteResponseDto dto = new ClienteResponseDto();
        UUID id = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        // Act
        dto.setId(id);
        dto.setNome("Nome Alterado");
        dto.setCpf("00000000000");
        dto.setCnpj("11444777000161");
        dto.setEmail("alterado@exemplo.com");
        dto.setStatus(StatusCliente.INATIVO);
        dto.setCriadoEm(agora);
        dto.setAtualizadoEm(agora);
        dto.setPessoaFisica(false);
        dto.setPessoaJuridica(true);
        dto.setQuantidadeVeiculos(99);
        dto.setTipoPessoa("PJ");
        dto.setDocumento("11444777000161");

        // Assert
        assertEquals(id, dto.getId());
        assertEquals("Nome Alterado", dto.getNome());
        assertEquals("00000000000", dto.getCpf());
        assertEquals("11444777000161", dto.getCnpj());
        assertEquals("alterado@exemplo.com", dto.getEmail());
        assertEquals(StatusCliente.INATIVO, dto.getStatus());
        assertEquals(agora, dto.getCriadoEm());
        assertEquals(agora, dto.getAtualizadoEm());
        assertFalse(dto.isPessoaFisica());
        assertTrue(dto.isPessoaJuridica());
        assertEquals(99, dto.getQuantidadeVeiculos());
        assertEquals("PJ", dto.getTipoPessoa());
        assertEquals("11444777000161", dto.getDocumento());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto com CPF e CNPJ nulos")
    void deveCriarClienteResponseDtoComCpfECnpjNulos() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Cliente Sem Documento")
                .email("cliente@exemplo.com")
                .status(StatusCliente.ATIVO)
                .isPessoaFisica(false)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getCpf());
        assertNull(dto.getCnpj());
        assertNull(dto.getDocumento());
        assertEquals("Não definido", dto.getTipoPessoa());
    }

    @Test
    @DisplayName("Deve criar ClienteResponseDto com timestamps nulos")
    void deveCriarClienteResponseDtoComTimestampsNulos() {
        // Arrange
        UUID id = UUID.randomUUID();
        ClienteResponse response = ClienteResponse.builder()
                .id(id)
                .nome("Cliente Sem Timestamp")
                .cpf("12345678909")
                .email("cliente@exemplo.com")
                .status(StatusCliente.ATIVO)
                .isPessoaFisica(true)
                .isPessoaJuridica(false)
                .quantidadeVeiculos(0)
                .build();

        // Act
        ClienteResponseDto dto = ClienteResponseDto.from(response);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getCriadoEm());
        assertNull(dto.getAtualizadoEm());
    }
}
