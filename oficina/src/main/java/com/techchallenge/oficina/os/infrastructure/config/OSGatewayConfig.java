package com.techchallenge.oficina.os.infrastructure.config;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarServicoInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.DebitarEstoqueMROInput;
import com.techchallenge.oficina.os.application.usecases.*;
import com.techchallenge.oficina.os.application.usecases.ports.input.*;
import com.techchallenge.oficina.os.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.ports.output.VeiculoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSGatewayConfig {

    @Bean
    public CriarOrdemServicoInpuit criarOrdemServicoInput(OrdemServicoGateway ordemServicoGateway, 
                                                          ClienteGateway clienteGateway, 
                                                          VeiculoGateway veiculoGateway) {
        return new CriarOrdemServicoUseCase(ordemServicoGateway, clienteGateway, veiculoGateway);
    }

    @Bean
    public ListarOrdensServicoInput listarOrdensServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                              BuscarServicoInput buscarServicoInput,
                                                              BuscarMROInput buscarMROInput) {
        return new ListarOrdensServicoUseCase(ordemServicoGateway, buscarServicoInput, buscarMROInput);
    }

    @Bean
    public BuscarOrdemServicoInput buscarOrdemServicoInput(OrdemServicoGateway ordemServicoGateway) {
        return new BuscarOrdemServicoUseCase(ordemServicoGateway);
    }

    @Bean
    public AdicionarServicoOrdemInput adicionarServicoOrdemInput(OrdemServicoGateway ordemServicoGateway,
                                                                  BuscarServicoInput buscarServicoInput) {
        return new AdicionarServicoOrdemUseCase(ordemServicoGateway, buscarServicoInput);
    }

    @Bean
    public RemoverServicoOrdemInput removerServicoOrdemInput(OrdemServicoGateway ordemServicoGateway) {
        return new RemoverServicoOrdemUseCase(ordemServicoGateway);
    }

    @Bean
    public AdicionarMROServicoInput adicionarMROServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                              BuscarMROInput buscarMROInput) {
        return new AdicionarMROServicoUseCase(ordemServicoGateway, buscarMROInput);
    }

    @Bean
    public RemoverMROServicoInput removerMROServicoInput(OrdemServicoGateway ordemServicoGateway) {
        return new RemoverMROServicoUseCase(ordemServicoGateway);
    }

    @Bean
    public AtualizarQuantidadeMROInput atualizarQuantidadeMROInput(OrdemServicoGateway ordemServicoGateway) {
        return new AtualizarQuantidadeMROUseCase(ordemServicoGateway);
    }

    @Bean
    public EnviarParaDiagnosticoInput enviarParaDiagnosticoInput(OrdemServicoGateway ordemServicoGateway) {
        return new EnviarParaDiagnosticoUseCase(ordemServicoGateway);
    }

    @Bean
    public EnviarOrcamentoAoClienteInput enviarOrcamentoAoClienteInput(OrdemServicoGateway ordemServicoGateway) {
        return new EnviarOrcamentoAoClienteUseCase(ordemServicoGateway);
    }

    @Bean
    public AtualizarObservacoesServicoInput atualizarObservacoesServicoInput(OrdemServicoGateway ordemServicoGateway) {
        return new AtualizarObservacoesServicoUseCase(ordemServicoGateway);
    }

    @Bean
    public IniciarServicoInput iniciarServicoInput(OrdemServicoGateway ordemServicoGateway, DebitarEstoqueMROInput debitarEstoqueMROInput) {
        return new IniciarServicoUseCase(ordemServicoGateway, debitarEstoqueMROInput);
    }

    @Bean
    public ConcluirServicoInput concluirServicoInput(OrdemServicoGateway ordemServicoGateway) {
        return new ConcluirServicoUseCase(ordemServicoGateway);
    }

    @Bean
    public CancelarServicoInput cancelarServicoInput(OrdemServicoGateway ordemServicoGateway) {
        return new CancelarServicoUseCase(ordemServicoGateway);
    }

    @Bean
    public EntregarOrdemServicoInput entregarOrdemServicoInput(OrdemServicoGateway ordemServicoGateway) {
        return new EntregarOrdemServicoUseCase(ordemServicoGateway);
    }

    @Bean
    public CalcularTempoMedioExecucaoInput calcularTempoMedioExecucaoInput(OrdemServicoGateway ordemServicoGateway) {
        return new CalcularTempoMedioExecucaoUseCase(ordemServicoGateway);
    }

    @Bean
    public AprovarOrcamentoInput aprovarOrcamentoInput(OrdemServicoGateway ordemServicoGateway){
        return new AprovarOrcamentoUseCase(ordemServicoGateway);
    }

    @Bean
    public ListarOrdensServicoPorClienteInput listarOrdensServicoPorClienteInput(OrdemServicoGateway ordemServicoGateway){
        return new ListarOrdensServicoPorClienteUseCase(ordemServicoGateway);
    }


}
