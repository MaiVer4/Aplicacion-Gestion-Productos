package com.app.gestion_negocio.client;

import com.app.gestion_negocio.dto.ProductoDto;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;

public class ClienteRest {
    private static final String BASE_URL = "http://localhost:8080/productos";
    private final RestTemplate restTemplate;

    public ClienteRest() {
        this.restTemplate = new RestTemplate();
    }

    public static void main(String[] args) {
        ClienteRest cliente = new ClienteRest();
        cliente.llamarServicioProducto();
    }

    private void llamarServicioProducto() {

    }

    public ProductoDto consultarProductoPorCodigo(String codigo) {
        try {
            ResponseEntity<ProductoDto> response = restTemplate.getForEntity(
                    BASE_URL + "/{codigo}", 
                    ProductoDto.class, 
                    codigo
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al consultar producto: " + e.getMessage());
            return null;
        }
    }

    public List<ProductoDto> consultarListaProductos() {
        try {
            ResponseEntity<ProductoDto[]> response = restTemplate.getForEntity(
                    BASE_URL, 
                    ProductoDto[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new ProductoDto[0]);
        } catch (Exception e) {
            System.err.println("Error al consultar lista: " + e.getMessage());
            return List.of();
        }
    }

    public ProductoDto registrarProducto(ProductoDto producto) {
        try {
            HttpEntity<ProductoDto> request = new HttpEntity<>(producto);
            ResponseEntity<ProductoDto> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.POST,
                    request,
                    ProductoDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al registrar producto: " + e.getMessage());
            return null;
        }
    }

    public ProductoDto actualizarProducto(ProductoDto producto) {
        try {
            HttpEntity<ProductoDto> request = new HttpEntity<>(producto);
            ResponseEntity<ProductoDto> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.PUT,
                    request,
                    ProductoDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return null;
        }
    }

    public boolean eliminarProducto(String codigo) {
        try {
            restTemplate.delete(BASE_URL + "/{codigo}", codigo);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}
