import * as ol from 'openlayers'

export function wgsToMap(coordinates: ol.Coordinate): ol.Coordinate {
    return ol.proj.transform(coordinates, 'EPSG:4326', 'EPSG:3857')
}

//RGBA
type olColor = [number, number, number, number]
const primary: olColor = [68, 138, 255, 255]; //blue
const secondary: olColor = [255, 82, 82, 255]; //red
const green: olColor = [0, 200, 83, 255];
const yellow: olColor = [255, 255, 0, 255];

export function currentPositionStyle(feature: ol.Feature) {
    return new ol.style.Style({
        ...(circleImage(secondary)),
        text: new ol.style.Text({
            text: feature.get('name'),
            font: '12px Roboto, sans-serif',
            offsetY: 20,
            fill: new ol.style.Fill({color: 'white'}),
            //@ts-ignore
            backgroundFill: new ol.style.Fill({color: secondary})
        })
    });
}

export function gasStrategyStyle(feature: ol.Feature) {
    if (feature.getGeometry().getType() === 'Point') {
        let color: olColor = primary;
        if (feature.getProperties()['isStart']) {
            color = green;
        } else if (feature.getProperties()['isEnd']) {
            color = secondary;
        }
        return new ol.style.Style({
            ...(circleImage(color))
        });
    } else {
        return new ol.style.Style({
            stroke: new ol.style.Stroke({color: primary})
        })
    }
}

export function predictionsStyle(feature: ol.Feature) {
    //@ts-ignore
    return new ol.style.Style({
        ...(circleImage(primary))
    });
}

function circleImage(color: olColor): olx.style.StyleOptions {
    return {
        image: new ol.style.Circle({
            fill: new ol.style.Fill({color}),
            stroke: new ol.style.Stroke({color}),
            radius: 5
        })
    }
}
