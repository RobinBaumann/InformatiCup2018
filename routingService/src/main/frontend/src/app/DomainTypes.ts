export class Route {
    capacity: number
    routePoints: RoutePoint[]

    constructor(capacity: number, routePoints: RoutePoint[]) {
        this.capacity = capacity;
        this.routePoints = routePoints;
    }
}

export class RoutePoint {
    stationId: number
    timestamp: Date

    constructor(stationId: number, timestamp: Date) {
        this.stationId = stationId
        this.timestamp = timestamp
    }
}

export class AppError implements DescribableError {
    description: string

    constructor(description: string) {
        this.description = description
    }

    describe(): string {
        return this.description
    }
}

export interface DescribableError {
    describe(): string
}


export class GasStrategy {
    stops: GasStop[]

    constructor(stops: GasStop[]) {
        this.stops = stops;
    }
}

export class GasStop {
    amount: number
    price: number
    timestamp: Date
    station: GasStation
}

export class GasStation {
    lat: number
    lon: number
    station_name: string
    id: number
    street: string
    brand: string
    house_number: string
    zip_code: string
    city: string
}

export class Problem implements DescribableError{
    type: string
    title: string
    detail: string
    status: number

    describe(): string {
        return `${this.title}\n${this.detail}`
    }
}

export enum Events {
    Error = 'ERROR',
    StrategyReceived = 'STRATEGY_RECEIVED'
}

