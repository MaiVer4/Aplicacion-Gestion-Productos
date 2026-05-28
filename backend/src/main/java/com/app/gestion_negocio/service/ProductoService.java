package com.app.gestion_negocio.service;

import com.app.gestion_negocio.dto.ProductoDto;
import com.app.gestion_negocio.entity.Producto;
import com.app.gestion_negocio.exception.BadRequestException;
import com.app.gestion_negocio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.app.gestion_negocio.repository.ProductoRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional
    public ProductoDto obtenerProductoPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo).map(this::convertToDto).orElseThrow(
                () -> new ResourceNotFoundException("Producto con codigo " + codigo + " no encontrado")
        );
    }

    @Transactional
    public List<ProductoDto> obtenerListaProductos() {
        return productoRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public ProductoDto registrarProducto(ProductoDto productoDto) {
        if (productoDto == null || productoDto.getCodigo() == null || productoDto.getCodigo().isEmpty()) {
            throw new BadRequestException("El código es obligatorio");
        }
        if (productoRepository.findByCodigo(productoDto.getCodigo()).isPresent()) {
            throw new BadRequestException("El producto ya existe");
        }

        if (productoDto.getImagenUrl() == null || productoDto.getImagenUrl().isBlank()) {
            throw new BadRequestException("La URL de la imagen es obligatoria");
        }

        Producto nuevoProducto = convertToEntity(productoDto);

        Producto guardado = productoRepository.save(nuevoProducto);
        return convertToDto(guardado);
    }

    @Transactional
    public ProductoDto actualizarProducto(ProductoDto productoDto) {
        if (productoDto == null || productoDto.getCodigo() == null || productoDto.getCodigo().isEmpty()) {
            throw new BadRequestException("El código es obligatorio para actualizar");
        }
        if (productoDto.getImagenUrl() == null || productoDto.getImagenUrl().isBlank()) {
            throw new BadRequestException("La URL de la imagen es obligatoria");
        }

        return productoRepository.findByCodigo(productoDto.getCodigo())
                .map(productoExistente -> {
                    productoExistente.setNombre(productoDto.getNombre());
                    productoExistente.setDescripcion(productoDto.getDescripcion());
                    productoExistente.setPrecio(productoDto.getPrecio());
                    productoExistente.setCantidad(productoDto.getCantidad());
                    productoExistente.setEstado(productoDto.getEstado());
                    productoExistente.setImagenUrl(productoDto.getImagenUrl());

                    Producto actualizado = productoRepository.save(productoExistente);
                    return convertToDto(actualizado);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto con codigo " + productoDto.getCodigo() + " no encontrado"));
    }

    @Transactional
    public void eliminarProducto(String codigo) {
        Producto producto = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con codigo " + codigo + " no encontrado"));

        productoRepository.delete(producto);
    }

    private ProductoDto convertToDto(Producto producto) {
        if (producto == null)
            return null;
        return new ProductoDto(
                producto.getCodigo(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCantidad(),
                producto.getEstado(),
                producto.getImagenUrl());
    }

    private Producto convertToEntity(ProductoDto dto) {
        if (dto == null)
            return null;
        Producto producto = new Producto();
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCantidad(dto.getCantidad());
        producto.setEstado(dto.getEstado());
        producto.setImagenUrl(dto.getImagenUrl());
        return producto;
    }
}
