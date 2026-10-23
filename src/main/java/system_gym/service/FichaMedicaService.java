package system_gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.FichaMedica;
import system_gym.repository.FichaMedicaRepository;

@Service
public class FichaMedicaService {

    private final FichaMedicaRepository fichaMedicaRepository;

    public FichaMedicaService(FichaMedicaRepository fichaMedicaRepository) {
        this.fichaMedicaRepository = fichaMedicaRepository;
    }

    @Transactional(readOnly = true)
    public List<FichaMedica> listar() {
        return fichaMedicaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public FichaMedica buscarPorId(Integer id) {
        return fichaMedicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ficha médica no encontrada con id " + id));
    }

    @Transactional
    public FichaMedica guardar(FichaMedica ficha) {
        return fichaMedicaRepository.save(ficha);
    }

    @Transactional
    public void eliminar(Integer id) {
        fichaMedicaRepository.deleteById(id);
    }
}
