package com.luka.gamesellerrating.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.function.Function;

@Component
public class MapperUtil {

    private final ModelMapper modelMapper;

    public MapperUtil(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setAmbiguityIgnored(true);
    }

    public <T> T convert(Object source, T destination) {
        return modelMapper.map(source, (Type) destination.getClass());
    }


    public <S, D> Function<S, D> convertTo(Class<D> destinationType) {
        return source -> modelMapper.map(source, destinationType);
    }
}
