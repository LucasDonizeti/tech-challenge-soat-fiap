package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.ClienteJpaMapper;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.specifications.ClienteFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClienteRepositoryImpl implements ClienteRepository {
    
    private final ClienteJpaRepository jpaRepository;
    private final ClienteJpaMapper mapper;
    
    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = mapper.toEntity(cliente);
        ClienteEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Cliente> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Cliente> findAll() {
        List<ClienteEntity> entities = jpaRepository.findAll();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Cliente> findByStatus(StatusCliente status) {
        ClienteEntity.StatusClienteEntity entityStatus = toEntityStatus(status);
        List<ClienteEntity> entities = jpaRepository.findByStatus(entityStatus);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public Optional<Cliente> findByCPF(CPF cpf) {
        return jpaRepository.findByCpf(cpf.getValor())
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Cliente> findByCNPJ(CNPJ cnpj) {
        return jpaRepository.findByCnpj(cnpj.getValor())
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Cliente> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getEndereco())
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByCPF(CPF cpf) {
        return jpaRepository.existsByCpf(cpf.getValor());
    }
    
    @Override
    public boolean existsByCNPJ(CNPJ cnpj) {
        return jpaRepository.existsByCnpj(cnpj.getValor());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getEndereco());
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public List<Cliente> findByFilter(Object filter) {
        if (filter instanceof ClienteFilter clienteFilter) {
            return findByClienteFilter(clienteFilter);
        }
        
        return findByLegacyFilter(filter);
    }

    private List<Cliente> findByClienteFilter(ClienteFilter clienteFilter) {
        List<ClienteEntity> entities = jpaRepository.findByFilter(
            clienteFilter.getNome(),
            clienteFilter.getCpf(),
            clienteFilter.getCnpj(),
            clienteFilter.getEmail(),
            clienteFilter.getStatus() != null ? toEntityStatus(clienteFilter.getStatus()) : null
        );
        return mapper.toDomainList(entities);
    }

    private List<Cliente> findByLegacyFilter(Object filter) {
        // Fallback para compatibilidade com DTOs existentes (temporário)
        // Nota: Usando instanceof para verificação de tipo
        try {
            Class<?> filterRequestClass = Class.forName("com.techchallenge.oficina.administrativo.web.dto.ClienteFilterRequest");
            if (!filterRequestClass.isInstance(filter)) {
                throw new IllegalArgumentException("Tipo de filtro não suportado: " + filter.getClass().getSimpleName());
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Tipo de filtro não suportado: " + filter.getClass().getSimpleName());
        }
        
        try {
            Object nome = filter.getClass().getMethod("getNome").invoke(filter);
            Object cpf = filter.getClass().getMethod("getCpf").invoke(filter);
            Object cnpj = filter.getClass().getMethod("getCnpj").invoke(filter);
            Object email = filter.getClass().getMethod("getEmail").invoke(filter);
            Object status = filter.getClass().getMethod("getStatus").invoke(filter);
            
            List<ClienteEntity> entities = jpaRepository.findByFilter(
                nome != null ? nome.toString() : null,
                cpf != null ? cpf.toString() : null,
                cnpj != null ? cnpj.toString() : null,
                email != null ? email.toString() : null,
                status != null ? toEntityStatus((StatusCliente) status) : null
            );
            return mapper.toDomainList(entities);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar filtro", e);
        }
    }
    
    private ClienteEntity.StatusClienteEntity toEntityStatus(StatusCliente status) {
        return switch (status) {
            case ATIVO -> ClienteEntity.StatusClienteEntity.ATIVO;
            case INATIVO -> ClienteEntity.StatusClienteEntity.INATIVO;
        };
    }
}
