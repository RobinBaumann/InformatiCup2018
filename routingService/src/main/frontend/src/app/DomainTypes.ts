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

export class AppError {
    description: string

    constructor(description: string) {
        this.description = description
    }
}