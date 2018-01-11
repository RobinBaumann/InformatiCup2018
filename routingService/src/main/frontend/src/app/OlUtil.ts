import * as ol from 'openlayers'

export function wgsToMap(coordinates: ol.Coordinate): ol.Coordinate {
    return ol.proj.transform(coordinates, 'EPSG:4326', 'EPSG:3857')
}
