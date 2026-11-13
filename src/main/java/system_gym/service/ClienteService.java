package system_gym.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import system_gym.model.Cliente;
import system_gym.repository.ClienteRepository;

/**
 * Servicio CRUD para los clientes del gimnasio.
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id " + id));
    }

    @Transactional
    public Cliente guardar(Cliente cliente) {
        normalizar(cliente);
        validar(cliente);
        if (cliente.getId() == null) {
            cliente.setFechaRegistro(LocalDateTime.now());
        } else {
            Cliente existente = buscarPorId(cliente.getId());
            cliente.setFechaRegistro(existente.getFechaRegistro());
        }
        return clienteRepository.save(cliente);
    }

    private void normalizar(Cliente cliente) {
        cliente.setNombre(limpiar(cliente.getNombre()));
        cliente.setApellido(limpiar(cliente.getApellido()));
        cliente.setTelefono(limpiar(cliente.getTelefono()));
        cliente.setDocumento(limpiar(cliente.getDocumento()));
        cliente.setCorreo(limpiar(cliente.getCorreo()));
        cliente.setDireccion(limpiar(cliente.getDireccion()));
        cliente.setCiudad(limpiar(cliente.getCiudad()));
        cliente.setContactoEmergencia(limpiar(cliente.getContactoEmergencia()));
        cliente.setTelefonoEmergencia(limpiar(cliente.getTelefonoEmergencia()));
        cliente.setOcupacion(limpiar(cliente.getOcupacion()));
        cliente.setObservacion(limpiar(cliente.getObservacion()));
    }

    private void validar(Cliente cliente) {
        if (esVacio(cliente.getNombre())) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (esVacio(cliente.getApellido())) {
            throw new IllegalArgumentException("El apellido del cliente es obligatorio.");
        }
        if (esVacio(cliente.getDocumento())) {
            throw new IllegalArgumentException("El documento del cliente es obligatorio.");
        }
        if (esVacio(cliente.getTelefono())) {
            throw new IllegalArgumentException("El teléfono del cliente es obligatorio.");
        }
        if (esVacio(cliente.getCorreo())) {
            throw new IllegalArgumentException("El correo del cliente es obligatorio.");
        }
        if (cliente.getSexo() == null || (cliente.getSexo() != 0 && cliente.getSexo() != 1)) {
            throw new IllegalArgumentException("El sexo del cliente es obligatorio.");
        }
        if (esVacio(cliente.getDireccion())) {
            throw new IllegalArgumentException("La dirección del cliente es obligatoria.");
        }
        if (esVacio(cliente.getCiudad())) {
            throw new IllegalArgumentException("La ciudad del cliente es obligatoria.");
        }
        if (esVacio(cliente.getContactoEmergencia())) {
            throw new IllegalArgumentException("El contacto de emergencia es obligatorio.");
        }
        if (esVacio(cliente.getTelefonoEmergencia())) {
            throw new IllegalArgumentException("El teléfono de emergencia es obligatorio.");
        }
        if (esVacio(cliente.getOcupacion())) {
            throw new IllegalArgumentException("La ocupación del cliente es obligatoria.");
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    @Transactional
    public void eliminar(Integer id) {
        clienteRepository.deleteById(id);
    }
}
