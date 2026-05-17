package com.store.store.utils;

import java.util.stream.Collectors;

import com.store.store.model.Category;
import com.store.store.model.CategoryResponse;
import com.store.store.model.Location;
import com.store.store.model.LocationResponse;

public class LocationMapper {

    public static Location toLocation(LocationResponse response) {
        Location location = new Location();
        location.setId(response.getId());
        location.setName(response.getName());
        location.setDescription(response.getDescription());
        location.setAddress(response.getAddress());
        location.setCity(response.getCity());
        location.setState(response.getState());
        location.setZip(response.getZip());
        location.setPhone(response.getPhone());
        location.setTarjetaBancomer(response.getTarjetaBancomer());
        location.setOxxo(response.getOxxo());

        // Use the provided list of IDs, or derive them from Category objects if needed
        if (response.getCategoriesList() != null && !response.getCategoriesList().isEmpty()) {
            location.setCategoriesList(response.getCategoriesList());
        } else if (response.getCategories() != null) {
            // Extract IDs from nested Category objects
            location.setCategoriesList(
                    response.getCategories().stream()
                            .map(CategoryResponse::getId)
                            .collect(Collectors.toList()));
        }

        return location;
    }

    public static LocationResponse toLocationResponse(Location location) {
        LocationResponse response = new LocationResponse();

        response.setId(location.getId());
        response.setName(location.getName());
        response.setDescription(location.getDescription());
        response.setAddress(location.getAddress());
        response.setCity(location.getCity());
        response.setState(location.getState());
        response.setZip(location.getZip());
        response.setPhone(location.getPhone());
        response.setTarjetaBancomer(location.getTarjetaBancomer());
        response.setOxxo(location.getOxxo());

        // Use the provided list of category IDs, or derive CategoryResponse objects if
        // needed
        if (location.getCategoriesList() != null && !location.getCategoriesList().isEmpty()) {
            response.setCategoriesList(location.getCategoriesList());
        }
        response.setCategories(null);

        return response;
    }

}
