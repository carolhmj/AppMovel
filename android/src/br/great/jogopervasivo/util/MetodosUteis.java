package br.great.jogopervasivo.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by messiaslima on 12/06/2015.
 *
 * @author messiaslima
 */
public class MetodosUteis {
    /**
     * Metodo que converte duas formas de representar localizacao
     *
     * @param latLng   objeto a ser convertido para {@code Location}
     * @param provider location provider do location
     * @return um objeto Location equivalente a o objeto {@code LatLng} passado como parametro
     */
    public static Location latLngToLocation(String provider, LatLng latLng) {
        Location location = new Location(provider);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    /**
     * Distancia em metros entre dois ponto de geolocalização
     *
     * @param posicaoA LatLng do primeiro ponto
     * @param posicaoB LatLng do segundo ponto
     * @return distancia dos pontos em metros
     */
    public double distanciaMetros(LatLng posicaoA, LatLng posicaoB) {
        int fatorMetros = 6371000;
        double PI = 3.14159265;
        return fatorMetros * Math.acos(Math.cos(PI * (90 - posicaoB.latitude) / 180) * Math.cos((90 - posicaoA.latitude) * PI / 180) + Math.sin((90 - posicaoB.latitude) * PI / 180) * Math.sin((90 - posicaoA.latitude) * PI / 180) * Math.cos((posicaoA.longitude - posicaoB.longitude) * PI / 180));
    }
}
