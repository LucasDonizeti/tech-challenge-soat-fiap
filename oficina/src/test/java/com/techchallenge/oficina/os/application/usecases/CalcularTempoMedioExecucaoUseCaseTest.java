package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.web.dto.TempoMedioExecucaoResponseDto;
import com.techchallenge.oficina.os.web.dto.TempoMedioExecucaoResponseDto.TempoServicoMetricas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Testes de CalcularTempoMedioExecucaoUseCase - Application Layer")
class CalcularTempoMedioExecucaoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private CalcularTempoMedioExecucaoUseCase useCase;

    private OrdemServico ordemServico1;
    private OrdemServico ordemServico2;

    @BeforeEach
    void setUp() {
        ordemServico1 = mock(OrdemServico.class);
        ordemServico2 = mock(OrdemServico.class);
    }

    @Test
    @DisplayName("Deve calcular tempo médio de execução com sucesso")
    void deveCalcularTempoMedioExecucaoComSucesso() {
        // Arrange
        when(ordemServico1.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(ordemServico1.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(ordemServico1.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));
        when(ordemServico1.getItensServico()).thenReturn(new ArrayList<>());

        when(ordemServico2.getStatus()).thenReturn(StatusOS.ENTREGUE);
        when(ordemServico2.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 2, 10, 0));
        when(ordemServico2.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 2, 14, 0));
        when(ordemServico2.getItensServico()).thenReturn(new ArrayList<>());

        List<OrdemServico> ordensServico = List.of(ordemServico1, ordemServico2);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        var response = useCase.execute(null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getQuantidadeOS());
        assertTrue(response.getTempoMedioSegundos() > 0);
        assertNotNull(response.getTempoMedioFormatado());
        assertTrue(response.getTempoMinimoSegundos() > 0);
        assertTrue(response.getTempoMaximoSegundos() > 0);
        assertNotNull(response.getTempoMedioPorServico());
        verify(ordemServicoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar zeros quando não há ordens de serviço")
    void deveRetornarZerosQuandoNaoHaOrdensServico() {
        // Arrange
        Page<OrdemServico> emptyPage = Page.empty();
        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        var response = useCase.execute(null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getQuantidadeOS());
        assertEquals(0L, response.getTempoMedioSegundos());
        assertEquals(0L, response.getTempoMinimoSegundos());
        assertEquals(0L, response.getTempoMaximoSegundos());
        verify(ordemServicoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve filtrar por período quando fornecido")
    void deveFiltrarPorPeriodoQuandoFornecido() {
        // Arrange
        LocalDateTime dataInicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime dataFim = LocalDateTime.of(2024, 1, 31, 23, 59);

        when(ordemServico1.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(ordemServico1.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(ordemServico1.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));
        when(ordemServico1.getItensServico()).thenReturn(new ArrayList<>());

        List<OrdemServico> ordensServico = List.of(ordemServico1);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        var response = useCase.execute(null, dataInicio, dataFim);

        // Assert
        assertNotNull(response);
        verify(ordemServicoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve ignorar ordens sem data de início ou finalização")
    void deveIgnorarOrdensSemDataInicioOuFinalizacao() {
        // Arrange
        when(ordemServico1.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(ordemServico1.getDataInicioExecucao()).thenReturn(null);
        when(ordemServico1.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));

        when(ordemServico2.getStatus()).thenReturn(StatusOS.ENTREGUE);
        when(ordemServico2.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 2, 10, 0));
        when(ordemServico2.getDataFinalizacao()).thenReturn(null);

        List<OrdemServico> ordensServico = List.of(ordemServico1, ordemServico2);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        var response = useCase.execute(null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getQuantidadeOS());
        verify(ordemServicoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve ignorar ordens com status diferente de FINALIZADA ou ENTREGUE")
    void deveIgnorarOrdensComStatusDiferenteDeFinalizadaOuEntregue() {
        // Arrange
        when(ordemServico1.getStatus()).thenReturn(StatusOS.RECEBIDA);
        when(ordemServico1.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(ordemServico1.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));

        when(ordemServico2.getStatus()).thenReturn(StatusOS.EM_EXECUCAO);
        when(ordemServico2.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 2, 10, 0));
        when(ordemServico2.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 2, 14, 0));

        List<OrdemServico> ordensServico = List.of(ordemServico1, ordemServico2);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        var response = useCase.execute(null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getQuantidadeOS());
        verify(ordemServicoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve calcular tempo médio por tipo de serviço")
    void deveCalcularTempoMedioPorTipoDeServico() {
        // Arrange
        ItemServico item1 = mock(ItemServico.class);
        ItemServico item2 = mock(ItemServico.class);
        ItemServico item3 = mock(ItemServico.class);
        
        when(item1.getServicoNome()).thenReturn("Troca de Óleo");
        when(item1.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(item1.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 30)); // 30 min = 1800 seg
        
        when(item2.getServicoNome()).thenReturn("Troca de Óleo");
        when(item2.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 2, 10, 0));
        when(item2.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 2, 11, 0)); // 60 min = 3600 seg
        
        when(item3.getServicoNome()).thenReturn("Alinhamento");
        when(item3.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 3, 10, 0));
        when(item3.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 3, 10, 45)); // 45 min = 2700 seg
        
        when(ordemServico1.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(ordemServico1.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(ordemServico1.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));
        when(ordemServico1.getItensServico()).thenReturn(List.of(item1, item2));

        when(ordemServico2.getStatus()).thenReturn(StatusOS.ENTREGUE);
        when(ordemServico2.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 3, 10, 0));
        when(ordemServico2.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 3, 12, 0));
        when(ordemServico2.getItensServico()).thenReturn(List.of(item3));

        List<OrdemServico> ordensServico = List.of(ordemServico1, ordemServico2);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        var response = useCase.execute(null, null, null);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTempoMedioPorServico());
        assertTrue(response.getTempoMedioPorServico().containsKey("Troca de Óleo"));
        assertTrue(response.getTempoMedioPorServico().containsKey("Alinhamento"));
        
        TempoServicoMetricas oleoMetricas = response.getTempoMedioPorServico().get("Troca de Óleo");
        assertEquals(2, oleoMetricas.getQuantidade());
        assertEquals(2700L, oleoMetricas.getTempoMedioSegundos()); // (1800 + 3600) / 2
        
        TempoServicoMetricas alinhamentoMetricas = response.getTempoMedioPorServico().get("Alinhamento");
        assertEquals(1, alinhamentoMetricas.getQuantidade());
        assertEquals(2700L, alinhamentoMetricas.getTempoMedioSegundos());
    }

    @Test
    @DisplayName("Deve ignorar serviços sem datas de execução")
    void deveIgnorarServicosSemDatasDeExecucao() {
        // Arrange
        ItemServico item1 = mock(ItemServico.class);
        ItemServico item2 = mock(ItemServico.class);
        
        when(item1.getServicoNome()).thenReturn("Troca de Óleo");
        when(item1.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(item1.getDataFinalizacao()).thenReturn(null); // Sem data de finalização
        
        when(item2.getServicoNome()).thenReturn("Alinhamento");
        when(item2.getDataInicioExecucao()).thenReturn(null); // Sem data de início
        when(item2.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 3, 10, 45));
        
        when(ordemServico1.getStatus()).thenReturn(StatusOS.FINALIZADA);
        when(ordemServico1.getDataInicioExecucao()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(ordemServico1.getDataFinalizacao()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));
        when(ordemServico1.getItensServico()).thenReturn(List.of(item1, item2));

        List<OrdemServico> ordensServico = List.of(ordemServico1);
        Page<OrdemServico> page = new PageImpl<>(ordensServico);

        when(ordemServicoRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        var response = useCase.execute(null, null, null);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTempoMedioPorServico());
        assertTrue(response.getTempoMedioPorServico().isEmpty()); // Ambos ignorados
    }
}
