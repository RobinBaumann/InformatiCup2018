import { GasStation, GasStop, GasStrategy} from "./DomainTypes";

//https://tools.ietf.org/html/rfc7946
export function toGeoJson(gasStrategy: GasStrategy): string {
    const features: Feature[] = [];
    const route: Coordinates[] = [];
    for (let stop of gasStrategy.stops) {
        const feature = new Feature(
            toPoint(stop.station),
            toProperties(stop),
            stop.station.id,
        );
        features.push(feature);
        route.push((<Point>feature.geometry).coordinates);
    }
    features.push(new Feature(
        new LineString(route),
        {},
        gasStrategy.name + Date()
    ));
    return JSON.stringify(new FeatureCollection(features))
}

function toPoint(station: GasStation): Point {
    return new Point([station.lon, station.lat])
}

function toProperties(stop: GasStop): any {
    return {
        amount: stop.amount,
        price: stop.price,
        ...stop.station
    }
}

type Coordinates = [number, number]

abstract class Geometry {
    readonly abstract type: string;
    readonly abstract coordinates: Coordinates|Coordinates[]
}

class Point extends Geometry {
    readonly type: string = 'Point'
    readonly coordinates: Coordinates

    constructor(coordinates: Coordinates) {
        super()
        this.coordinates = coordinates
    }
}

class LineString extends Geometry {
    readonly type: string = 'LineString'
    readonly coordinates: Coordinates[]

    constructor(coordinates: Coordinates[]) {
        super()
        this.coordinates = coordinates
    }
}

class FeatureCollection {
    readonly type: string = 'FeatureCollection'
    readonly features: Feature[]

    constructor(features: Feature[]) {
        this.features = features
    }
}

class Feature {
    readonly type: string = 'Feature'
    readonly geometry: Geometry
    readonly properties: any
    readonly id: string|number


    constructor(geometry: Geometry, properties: any, id: string|number) {
        this.geometry = geometry
        this.properties = properties
        this.id = id
    }
}

