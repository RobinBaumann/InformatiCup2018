import * as ol from 'openlayers'

export function wgsToMap(coordinates: ol.Coordinate): ol.Coordinate {
    return ol.proj.transform(coordinates, 'EPSG:4326', 'EPSG:3857')
}

const color1: [number, number, number, number] = [68, 138, 255, 255]

export function currentPositionStyle(feature: ol.Feature, resolution: number) {
    return new ol.style.Style({
                image: new ol.style.Circle({
                    fill: new ol.style.Fill({color: color1}),
                    stroke: new ol.style.Stroke({color: 'black'}),
                    radius: 5
                }),
                text: new ol.style.Text({
                    text: feature.get('name'),
                    font: '12px Roboto, sans-serif',
                    offsetY: 20,
                    fill: new ol.style.Fill({color: 'black'}),
                    //@ts-ignore
                    backgroundFill: new ol.style.Fill({color: color1})
                })
            });
}
