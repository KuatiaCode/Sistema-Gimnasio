package system_gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.MembresiaCliente;
import system_gym.repository.MembresiaClienteRepository;

@Service
public class MembresiaClienteService {

    private final MembresiaClienteRepository membresiaClienteRepository;

    public MembresiaClienteService(MembresiaClienteRepository membresiaClienteRepository) {
        this.membresiaClienteRepository = membresiaClienteRepository;
    }

    @Transactional(readOnly = true)
    public List<MembresiaCliente> listar() {
        return membresiaClienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MembresiaCliente buscarPorId(Integer id) {
        return membresiaClienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada con id " + id));
    }

    @Transactional
    public MembresiaCliente guardar(MembresiaCliente membresia) {
        return membresiaClienteRepository.save(membresia);
    }

    @Transactional
    public void eliminar(Integer id) {
        membresiaClienteRepository.deleteById(id);
    }
}
