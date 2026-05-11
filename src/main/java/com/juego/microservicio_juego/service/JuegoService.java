package com.juego.microservicio_juego.service;

import com.juego.microservicio_juego.client.AuditoriaClient;
import com.juego.microservicio_juego.dto.AuditoriaRequestDTO;
import com.juego.microservicio_juego.dto.AuditoriaResponseDTO;
import com.juego.microservicio_juego.dto.JuegoRequestDTO;
import com.juego.microservicio_juego.dto.JuegoResponseDTO;
import com.juego.microservicio_juego.model.Juego;
import com.juego.microservicio_juego.model.Plataforma;
import com.juego.microservicio_juego.repository.JuegoRepository;
import com.juego.microservicio_juego.repository.PlataformaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JuegoService {

    private final JuegoRepository juegoRepository;
    private final PlataformaRepository plataformaRepository;
    private final AuditoriaClient auditoriaClient;

    private JuegoResponseDTO mapToDTO(Juego juego){
        return new JuegoResponseDTO(
            juego.getIdJuego(),
            juego.getNombre(),
            juego.getGenero(),
            juego.getDistribuidor(),
            juego.getPlataformas().stream().map(Plataforma::getIdPlataforma).collect(Collectors.toSet())
        );
    }

    public List<JuegoResponseDTO> obtenerJuegos(){
        return juegoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<JuegoResponseDTO> obtenerJuegoPorId(Long id){
        return juegoRepository.findById(id).map(this::mapToDTO);
    }

    public JuegoResponseDTO agregarJuego(JuegoRequestDTO dto){
        Set<Plataforma> idPlataforma = dto.getIdPlataformas().stream()
                .map(plataformaId -> plataformaRepository.findById(plataformaId)
                        .orElseThrow(()-> new RuntimeException("Plataforma no encontrada: "+plataformaId)))
                .collect(Collectors.toSet());

        Juego juego = new Juego(null, dto.getNombre(), dto.getGenero(), dto.getDistribuidor(), idPlataforma);
        log.info("Juego creado con exito!");
        generarAuditoria("Juego creado");
        return mapToDTO(juegoRepository.save(juego));
    }

    public Optional<JuegoResponseDTO> modificarJuego(Long id, JuegoRequestDTO dto){
        return juegoRepository.findById(id).map(existente -> {
            existente.setNombre(dto.getNombre());
            existente.setGenero(dto.getGenero());
            existente.setDistribuidor(dto.getDistribuidor());

            Set<Plataforma> idPlataforma = dto.getIdPlataformas().stream()
                    .map(plataformaId-> plataformaRepository.findById(plataformaId).
                            orElseThrow(() -> new RuntimeException("Plataforma no encontrada: "+plataformaId)))
                    .collect(Collectors.toSet());

            existente.setPlataformas(idPlataforma);
            log.info("Juego modificado con exito!");
            generarAuditoria("Juego modificado");
            return mapToDTO(juegoRepository.save(existente));
        });
    }

    public void eliminarJuego(Long id){
        juegoRepository.deleteById(id);
        log.info("Juego eliminado con exito!");
        generarAuditoria("Juego eliminado");
    }

    public void generarAuditoria(String detalle){
        AuditoriaRequestDTO dto = new AuditoriaRequestDTO();
        LocalDate ahora = LocalDate.now();
        dto.setDetalle(detalle);
        dto.setFecha(ahora);

        AuditoriaResponseDTO respuesta = auditoriaClient.generarAuditoria(dto);
    }


}
