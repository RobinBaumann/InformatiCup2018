import {GasStation, GasStop, GasStrategy, PricePrediction, PricePredictions} from "./DomainTypes";

//https://tools.ietf.org/html/rfc7946
export function strategyToGeoJson(gasStrategy: GasStrategy): string {
    const features: Feature[] = [];
    const route: Coordinates[] = [];
    for (let stop of gasStrategy.stops) {
        const feature = new Feature(
            toPoint(stop.station),
            stopToProperties(stop),
            stop.station.id,
        );
        features.push(feature);
        route.push((<Point>feature.geometry).coordinates);
    }
    features[0].properties['isStart'] = true;
    features[features.length - 1].properties['isEnd'] = true;
    features.push(new Feature(
        new LineString(route),
        {},
        gasStrategy.name + Date()
    ));
    return JSON.stringify(new FeatureCollection(features))
}

export function predictionsToGeoJson(pricePredictions: PricePredictions): string {
    const features = pricePredictions.predictions.map(p => new Feature(
        toPoint(p.station),
        predictionToProperties(p),
        p.station.id
    ));
    return JSON.stringify(new FeatureCollection(features))
}

function toPoint(station: GasStation): Point {
    return new Point([station.lon, station.lat])
}

function stopToProperties(stop: GasStop): any {
    return {
        amount: stop.amount,
        price: stop.price,
        ...stop.station
    };
}

function predictionToProperties(prediction: PricePrediction): any {
    return {
        momentKnown: prediction.momentKnownPrices,
        momentPrediction: prediction.momentPrediction,
        price: prediction.price,
        ...prediction.station
    };
}

type Coordinates = [number, number]

abstract class Geometry {
    readonly abstract type: string;
    readonly abstract coordinates: Coordinates | Coordinates[];
}

class Point extends Geometry {
    readonly type: string = 'Point';
    readonly coordinates: Coordinates;

    constructor(coordinates: Coordinates) {
        super();
        this.coordinates = coordinates;
    }
}

class LineString extends Geometry {
    readonly type: string = 'LineString';
    readonly coordinates: Coordinates[];

    constructor(coordinates: Coordinates[]) {
        super();
        this.coordinates = coordinates;
    }
}

class FeatureCollection {
    readonly type: string = 'FeatureCollection';
    readonly features: Feature[];
    readonly bbox: Extent;

    constructor(features: Feature[]) {
        this.features = features;
        this.bbox = FeatureCollection.calculateExtent(features);
    }

    private static calculateExtent(features: Feature[]): Extent {
        let minLat: number = Infinity;
        let maxLat: number = -Infinity;
        let minLon: number = Infinity;
        let maxLon: number = -Infinity;
        for (let feature of features) {
            if (feature.geometry instanceof Point) {
                const lon = feature.geometry.coordinates[0];
                const lat = feature.geometry.coordinates[1];
                if (lat < minLat) {
                    minLat = lat;
                }
                if (lat > maxLat) {
                    maxLat = lat
                }
                if (lon < minLon) {
                    minLon = lon;
                }
                if (lon > maxLon) {
                    maxLon = lon;
                }
            }
        }
        return [minLon, minLat, maxLon, maxLat]
    }
}

class Feature {
    readonly type: string = 'Feature';
    readonly geometry: Geometry;
    readonly properties: any;
    readonly id: string | number;


    constructor(geometry: Geometry, properties: any, id: string | number) {
        this.geometry = geometry;
        this.properties = properties;
        this.id = id;
    }
}

type Extent = [number, number, number, number]

