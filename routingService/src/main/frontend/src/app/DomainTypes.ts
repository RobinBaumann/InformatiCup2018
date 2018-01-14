export class Route {
    readonly capacity: number;
    readonly routePoints: RoutePoint[];
    readonly name: string;

    constructor(capacity: number, routePoints: RoutePoint[], name: string) {
        this.capacity = capacity;
        this.routePoints = routePoints;
        this.name = name;
    }
}

export class RoutePoint {
    readonly stationId: number;
    readonly timestamp: Date;

    constructor(stationId: number, timestamp: Date) {
        this.stationId = stationId;
        this.timestamp = timestamp;
    }
}

export class AppError implements DescribableError {
    readonly description: string;

    constructor(description: string) {
        this.description = description;
    }

    describe(): string {
        return this.description;
    }
}

export interface DescribableError {
    describe(): string;
}


export class GasStrategy {
    readonly stops: GasStop[];
    readonly name: string;
    readonly capacity: number;

    constructor(stops: GasStop[], name: string, capacity: number) {
        this.stops = stops;
        this.name = name;
        this.capacity = capacity;
    }
}

export class GasStop {
    //TODO make immutable
    amount: number;
    price: number;
    timestamp: Date;
    station: GasStation;
}

export class GasStation {
    //TODO make immutable
    lat: number;
    lon: number;
    station_name: string;
    id: number;
    street: string;
    brand: string;
    house_number: string;
    zip_code: string;
    city: string;
}

export class Problem implements DescribableError{
    readonly type: string;
    readonly title: string;
    readonly detail: string;
    readonly status: number;

    constructor(type: string, title: string, detail: string, status: number) {
        this.type = type;
        this.title = title;
        this.detail = detail;
        this.status = status;
    }

    describe(): string {
        return `${this.title}\n${this.detail}`;
    }
}

export class PricePredictionRequests {
    readonly predictionRequests: PricePredictionRequest[];
    readonly name: string;

    constructor(predictionRequests: PricePredictionRequest[], name: string) {
        this.predictionRequests = predictionRequests;
        this.name = name;
    }
}

export class PricePredictionRequest {
    readonly momentKnownPrices: Date;
    readonly momentPrediction: Date;
    readonly stationId: number;

    constructor(momentKnownPrices: Date, momentPrediction: Date, stationId: number) {
        this.momentKnownPrices = momentKnownPrices;
        this.momentPrediction = momentPrediction;
        this.stationId = stationId;
    }
}

export class PricePredictions {
    readonly name: string;
    readonly predictions: PricePrediction[];

    constructor(name: string, predictions: PricePrediction[]) {
        this.name = name;
        this.predictions = predictions;
    }
}

export class PricePrediction {
    readonly momentKnownPrices: Date;
    readonly momentPrediction: Date;
    readonly station: GasStation;
    readonly price: number;

    constructor(momentKnownPrices: Date, momentPrediction: Date, station: GasStation, price: number) {
        this.momentKnownPrices = momentKnownPrices;
        this.momentPrediction = momentPrediction;
        this.station = station;
        this.price = price;
    }
}

export abstract class Detail<T> {
    data: T;
    layer: ol.layer.Vector;
    removeHandler: (detail: Detail<T>) => void

    constructor(data: T, layer: ol.layer.Vector, removeHandler: (detail: Detail<T>) => void) {
        this.data = data;
        this.layer = layer;
        this.removeHandler = removeHandler;
    }

    abstract toCsv(): string;

    abstract toName(): string;

    createDataUri(content: string): string {
        return `data:text/plain;charset=utf8,${encodeURI(content)}`
    }

    download(event: MouseEvent) {
        let button: HTMLButtonElement|null = null;
        if (!event.srcElement) {
            return;
        }
        if (event.srcElement.tagName === 'button') {
            button = <HTMLButtonElement>event.srcElement;
        } else {
            if (event.srcElement.parentElement) {
                button = <HTMLButtonElement> event.srcElement.parentElement;
            }
        }
        if (!button) {
            return;
        }
        const link = button.querySelector('a');
        if (link) {
            link.click();
        }
    }

    remove() {
        this.removeHandler(this);
    }
}

export enum Events {
    Error = 'error',
    StrategyReceived = 'strategy_received',
    PredictionsReceived = 'predictions_received'
}

