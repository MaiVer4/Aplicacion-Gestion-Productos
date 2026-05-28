package com.app.gestion_negocio.controller;

import com.app.gestion_negocio.dto.ProductoDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.app.gestion_negocio.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Listado de productos
    @Transactional
    @GetMapping("")
    public ResponseEntity<List<ProductoDto>> obtenerListaProductos() {
        List<ProductoDto> lista = productoService.obtenerListaProductos();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // Obtener producto por Id
    @Transactional
    @GetMapping("/{codigo}")
    public ResponseEntity<?> consultarProductoPorPath(@PathVariable("codigo") String codigo) {
        ProductoDto producto = productoService.obtenerProductoPorCodigo(codigo);
        return ResponseEntity.ok(producto);
    }


    // Registrar producto
    @Transactional
    @PostMapping("")
    public ResponseEntity<ProductoDto> registrarProducto(@RequestBody @Valid ProductoDto productoDto) {
        ProductoDto registrado = productoService.registrarProducto(productoDto);
        return ResponseEntity.ok(registrado);
    }


    // Actualizar producto
    @Transactional
    @PutMapping("/{codigo}")
    public ResponseEntity<?> actualiazarProducto(@PathVariable("codigo") String codigo, @RequestBody @Valid ProductoDto productoDto) {
        productoDto.setCodigo(codigo);
        ProductoDto actualizado = productoService.actualizarProducto(productoDto);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar producto
    @Transactional
    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> eliminarProducto(@PathVariable("codigo") String codigo) {
        productoService.eliminarProducto(codigo);
        return ResponseEntity.ok("Producto eliminada con éxito.");
    }

}
