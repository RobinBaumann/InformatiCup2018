export class Route {
    capacity: number
    routePoints: RoutePoint[]
}

export class RoutePoint {
    stationId: number
    timestamp: Date
}

export class AppError {
    description: string
}