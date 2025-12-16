package com.appstock.appstock.config;

import com.appstock.appstock.dto.ProductoDTO;
import com.appstock.appstock.entity.Producto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig
{
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configuración para que sea inteligente con los IDs
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // LECCIÓN 1: De Entidad a DTO
        // "Cuando pases de Producto a ProductoDTO, saca el ID de la categoría y ponlo en categoriaId"
        modelMapper.typeMap(Producto.class, ProductoDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getCategoria().getId(), ProductoDTO::setCategoriaId);
                });

        // LECCIÓN 2: De DTO a Entidad
        // "Cuando pases de DTO a Producto, ignora la categoría (la pondremos a mano en el controller)"
        modelMapper.typeMap(ProductoDTO.class, Producto.class)
                .addMappings(mapper -> {
                    mapper.skip(Producto::setCategoria);
                });

        return modelMapper;
    }
}