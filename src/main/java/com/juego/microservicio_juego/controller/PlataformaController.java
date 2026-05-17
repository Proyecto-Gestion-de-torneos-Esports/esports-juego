package com.juego.microservicio_juego.controller;

import com.juego.microservicio_juego.model.Plataforma;
import com.juego.microservicio_juego.service.PlataformaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/plataforma")
public class PlataformaController {

    private final PlataformaService plataformaService;

    @GetMapping
    public ResponseEntity<List<Plataforma>> obtenerTodas(){
        return ResponseEntity.status(HttpStatus.OK).body(plataformaService.obtenerPlataformas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plataforma> buscarPorId(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(plataformaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Plataforma> agregarPlataforma(@Valid @RequestBody Plataforma plataforma){
        return ResponseEntity.status(HttpStatus.CREATED).body(plataformaService.agregarPlataforma(plataforma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPlataforma(@PathVariable Long id){
        plataformaService.eliminarPlataforma(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
