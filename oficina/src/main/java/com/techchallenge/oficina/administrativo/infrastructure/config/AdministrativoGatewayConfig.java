package com.techchallenge.oficina.administrativo.infrastructure.config;

import com.techchallenge.oficina.administrativo.application.usecases.*;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.*;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.MROGateway;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.administrativo.domain.services.ClienteDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdministrativoGatewayConfig {

    @Bean
    public CriarMROInput criarMROInput(MROGateway mroGateway){
        return new CriarMROUseCase(mroGateway);
    }

    @Bean
    public CriarClienteInput criarClienteInput(ClienteGateway clienteGateway, ClienteDomainService domainService) {
        return new CriarClienteUseCase(clienteGateway, domainService);
    }

    @Bean
    public AtualizarClienteInput atualizarClienteInput(ClienteGateway clienteGateway, ClienteDomainService domainService) {
        return new AtualizarClienteUseCase(clienteGateway, domainService);
    }

    @Bean
    public BuscarClienteInput buscarClienteInput(ClienteGateway clienteGateway) {
        return new BuscarClienteUseCase(clienteGateway);
    }

    @Bean
    public BuscarClientesPorFiltroInput buscarClientesPorFiltroInput(ClienteGateway clienteGateway) {
        return new BuscarClientesPorFiltroUseCase(clienteGateway);
    }

    @Bean
    public DeletarClienteInput deletarClienteInput(ClienteGateway clienteGateway, ClienteDomainService domainService) {
        return new DeletarClienteUseCase(clienteGateway, domainService);
    }

    @Bean
    public InativarClienteInput inativarClienteInput(ClienteGateway clienteGateway) {
        return new InativarClienteUseCase(clienteGateway);
    }

    @Bean
    public ListarClientesInput listarClientesInput(ClienteGateway clienteGateway) {
        return new ListarClientesUseCase(clienteGateway);
    }

    @Bean
    public ReativarClienteInput reativarClienteInput(ClienteGateway clienteGateway) {
        return new ReativarClienteUseCase(clienteGateway);
    }

    @Bean
    public CriarVeiculoInput criarVeiculoInput(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway) {
        return new CriarVeiculoUseCase(veiculoGateway, clienteGateway);
    }

    @Bean
    public BuscarVeiculoInput buscarVeiculoInput(VeiculoGateway veiculoGateway) {
        return new BuscarVeiculoUseCase(veiculoGateway);
    }

    @Bean
    public BuscarVeiculosPorClienteInput buscarVeiculosPorClienteInput(VeiculoGateway veiculoGateway) {
        return new BuscarVeiculosPorClienteUseCase(veiculoGateway);
    }

    @Bean
    public InativarVeiculoInput inativarVeiculoInput(VeiculoGateway veiculoGateway) {
        return new InativarVeiculoUseCase(veiculoGateway);
    }

    @Bean
    public ListarVeiculosInput listarVeiculosInput(VeiculoGateway veiculoGateway) {
        return new ListarVeiculosUseCase(veiculoGateway);
    }

    @Bean
    public ReativarVeiculoInput reativarVeiculoInput(VeiculoGateway veiculoGateway) {
        return new ReativarVeiculoUseCase(veiculoGateway);
    }

    @Bean
    public BuscarMROInput buscarMROInput(MROGateway mroGateway) {
        return new BuscarMROUseCase(mroGateway);
    }

    @Bean
    public BuscarServicoInput buscarServicoInput(ServicoGateway servicoGateway) {
        return new BuscarServicoUseCase(servicoGateway);
    }

    @Bean
    public ListarMROsInput listarMrosInput(MROGateway mroGateway){
        return new ListarMROsUseCase(mroGateway);
    }

    @Bean
    public InativarMROInput inativarMroInput(MROGateway mroGateway){
        return new InativarMROUseCase(mroGateway);
    }

    @Bean
    public AtivarMROInput ativarMroInput(MROGateway mroGateway){
        return new AtivarMROUseCase(mroGateway);
    }

    @Bean
    public AtualizarPrecoMROInput atualizarPrecoMROInput(MROGateway mroGateway) {
        return new AtualizarPrecoMROUseCase(mroGateway);
    }

    @Bean
    public AtualizarDadosMROInput atualizarDadosMROInput(MROGateway mroGateway) {
        return new AtualizarDadosMROUseCase(mroGateway);
    }

    @Bean
    public ReporEstoqueMROInput reporEstoqueMROInput(MROGateway mroGateway) {
        return new ReporEstoqueMROUseCase(mroGateway);
    }

    @Bean
    public DebitarEstoqueMROInput debitarEstoqueMROInput(MROGateway mroGateway){
        return new DebitarEstoqueMROUseCase(mroGateway);
    }

    @Bean
    public CriarServicoInput criarServicoInput(ServicoGateway servicoGateway) {
        return new CriarServicoUseCase(servicoGateway);
    }

    @Bean
    public ListarServicosInput listarServicosInput(ServicoGateway servicoGateway) {
        return new ListarServicosUseCase(servicoGateway);
    }

    @Bean
    public InativarServicoInput inativarServicoInput(ServicoGateway servicoGateway) {
        return new InativarServicoUseCase(servicoGateway);
    }

    @Bean
    public AtivarServicoInput ativarServicoInput(ServicoGateway servicoGateway) {
        return new AtivarServicoUseCase(servicoGateway);
    }

    @Bean
    public AtualizarPrecoServicoInput atualizarPrecoServicoInput(ServicoGateway servicoGateway) {
        return new AtualizarPrecoServicoUseCase(servicoGateway);
    }

    @Bean
    public AtualizarDadosServicoInput atualizarDadosServicoInput(ServicoGateway servicoGateway) {
        return new AtualizarDadosServicoUseCase(servicoGateway);
    }

}
