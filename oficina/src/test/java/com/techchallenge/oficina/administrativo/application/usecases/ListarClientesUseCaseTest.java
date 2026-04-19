package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ListarClientesUseCase - Application Layer")
class ListarClientesUseCaseTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ListarClientesUseCase useCase;

    private List<Cliente> clientes;

    @BeforeEach
    void setUp() {
        clientes = List.of(
                Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@teste.com")),
                Cliente.criar(Nome.of("Maria Santos"), CPF.of("98765432100"), Email.of("maria@teste.com")),
                Cliente.criar(Nome.of("Pedro Costa"), CPF.of("11144477735"), Email.of("pedro@teste.com"))
        );
    }

    @Test
    @DisplayName("Deve listar clientes com paginação padrão")
    void deveListarClientesComPaginacaoPadrao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> pageClientes = new PageImpl<>(clientes, pageable, clientes.size());
        when(repository.findAll(pageable)).thenReturn(pageClientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getNome());
        assertEquals("Maria Santos", result.getContent().get(1).getNome());
        assertEquals("Pedro Costa", result.getContent().get(2).getNome());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve aplicar paginação corretamente")
    void deveAplicarPaginacaoCorretamente() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        Page<Cliente> pageClientes = new PageImpl<>(clientes.subList(0, 2), pageable, clientes.size());
        when(repository.findAll(pageable)).thenReturn(pageClientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // Total de elementos
        assertEquals(2, result.getContent().size()); // Elementos na página
        assertEquals(2, result.getSize()); // Tamanho da página
        assertEquals(0, result.getNumber()); // Número da página

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há clientes")
    void deveRetornarPaginaVaziaQuandoNaoHaClientes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> pageClientes = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(pageClientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        assertTrue(result.getContent().isEmpty());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar segunda página corretamente")
    void deveListarSegundaPaginaCorretamente() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2); // Segunda página, tamanho 2
        Page<Cliente> pageClientes = new PageImpl<>(clientes.subList(2, 3), pageable, clientes.size());
        when(repository.findAll(pageable)).thenReturn(pageClientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getContent().size()); // Apenas 1 elemento na segunda página
        assertEquals("Pedro Costa", result.getContent().get(0).getNome());
        assertEquals(1, result.getNumber()); // Número da página

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar clientes pessoa jurídica")
    void deveListarClientesPessoaJuridica() {
        // Arrange
        var clientesPJ = List.of(
                Cliente.criarPJ(Nome.of("Auto Peças Ltda"), 
                        com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of("12345678000195"), 
                        Email.of("contato@autopecas.com.br")),
                Cliente.criarPJ(Nome.of("Mecânica Express"), 
                        com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of("04252011000110"), 
                        Email.of("contato@mecanicaexpress.com.br"))
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> pageClientesPJ = new PageImpl<>(clientesPJ, pageable, clientesPJ.size());
        when(repository.findAll(pageable)).thenReturn(pageClientesPJ);

        // Act
        Page<ClienteResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().get(0).isPessoaJuridica());
        assertTrue(result.getContent().get(1).isPessoaJuridica());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar clientes mistos (PF e PJ)")
    void deveListarClientesMistos() {
        // Arrange
        var clientesMistos = List.of(
                Cliente.criar(Nome.of("João Silva"), CPF.of("12345678909"), Email.of("joao@teste.com")),
                Cliente.criarPJ(Nome.of("Auto Peças Ltda"), 
                        com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ.of("12345678000195"), 
                        Email.of("contato@autopecas.com.br"))
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> pageClientesMistos = new PageImpl<>(clientesMistos, pageable, clientesMistos.size());
        when(repository.findAll(pageable)).thenReturn(pageClientesMistos);

        // Act
        Page<ClienteResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().get(0).isPessoaFisica());
        assertTrue(result.getContent().get(1).isPessoaJuridica());

        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pageable nulo")
    void deveLancarExcecaoQuandoPageableNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }

    @Test
    @DisplayName("Deve listar clientes com página além do conteúdo")
    void deveListarClientesComPaginaAlemDoConteudo() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 5); // Página além do conteúdo
        Page<Cliente> pageClientes = new PageImpl<>(List.of(), pageable, clientes.size());
        when(repository.findAll(pageable)).thenReturn(pageClientes);

        // Act
        Page<ClienteResponse> result = useCase.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getContent().size()); // Página vazia
        assertEquals(2, result.getNumber()); // Número da página solicitada

        verify(repository, times(1)).findAll(pageable);
    }
}
