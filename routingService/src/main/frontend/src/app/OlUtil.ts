import * as ol from 'openlayers'

export function wgsToMap(coordinates: ol.Coordinate): ol.Coordinate {
    return ol.proj.transform(coordinates, 'EPSG:4326', 'EPSG:3857')
}

//RGBA
type olColor = [number, number, number, number]
const color1: olColor = [68, 138, 255, 255]
const color2: olColor = [255, 193, 7, 255]

export function currentPositionStyle(feature: ol.Feature, resolution: number) {
    return new ol.style.Style({
                ...(circleImage(color1)),
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

export function gasStrategyStyle(feature: ol.Feature, resolution: number) {
    //@ts-ignore
    return strategyStyles[feature.getGeometry().getType()]
}

const strategyStyles = {
    'Point': new ol.style.Style({
        ...(circleImage(color2))
    })
}

function circleImage(color: olColor): olx.style.StyleOptions {
    return {
        image: new ol.style.Circle({
            fill: new ol.style.Fill({color}),
            stroke: new ol.style.Stroke({color: 'black'}), //TODO maybe parameterize
            radius: 5
        })
    }
}
