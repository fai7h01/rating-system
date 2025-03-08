package com.luka.gamesellerrating.util;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.AnonymousRating;
import com.luka.gamesellerrating.entity.AuthorizedRating;
import com.luka.gamesellerrating.entity.Rating;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class MapperUtil {

    private final ModelMapper modelMapper;

    public MapperUtil(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setAmbiguityIgnored(true);

        this.modelMapper.addMappings(new PropertyMap<AuthorizedRating, RatingDTO>() {
            @Override
            protected void configure() {
                skip(destination.getAnonymousAuthor());
            }
        });
        this.modelMapper.addMappings(new PropertyMap<AnonymousRating, RatingDTO>() {
            @Override
            protected void configure() {
                skip(destination.getAuthor());
            }
        });
    }

    public <T> T convert(Object source, T destination) {
        return modelMapper.map(source, (Type) destination.getClass());
    }

}
