package br.great.jogopervasivo.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by messiaslima on 12/06/2015.
 * @author messiaslima
 */
public class Conversor {
    /**
     * Metodo que converte duas formas de representar localizacao
     * @param latLng objeto a ser convertido para {@code Location}
     * @param provider location provider do location
     * @return um objeto Location equivalente a o objeto {@code LatLng} passado como parametro*/
    public static Location latLngToLocation(String provider, LatLng latLng){
        Location location = new Location(provider);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }
}
