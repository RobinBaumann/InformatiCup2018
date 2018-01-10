import {AppError, Route, RoutePoint} from "./DomainTypes";

export class CsvProcessor {
    processCsv(content:string): Route|AppError {
        const lines = content.split('\n'); //TODO check OS compat
        if (lines.length < 2) {
            return {
                description: "Csv file should contain at least 2 lines (capacity and one stop)."
            }
        }
        const capacity = parseInt(lines[0]);
        if (isNaN(capacity) || capacity < 0) {
            return {
                description: "First line must be capacity in litres (positive integer)."
            }
        }
        const stops: RoutePoint[] = [];
        for (let i = 1; i < lines.length; i++) {
            const result = this.parseRouteLine(lines[i]);
            if (result instanceof AppError) {
                return {description: `Error in line ${i + 1}: ${result.description}`}
            } else {
                stops.push(result)
            }
        }
        return {
            capacity: capacity,
            routePoints: stops
        }
    }

    parseRouteLine(line: string): RoutePoint|AppError {
        const parts = line.split(';');
        if (parts.length !== 2) {
            return {
                description: 'should consist of two elements separated by ";".'
            }
        }
        const date = Date.parse(parts[0]);
        if (isNaN(date)) {
            return {
                description: 'timestamp can not be parsed.'
            }
        }
        const timestamp = new Date();
        timestamp.setMilliseconds(date);
        const stationId = parseInt(parts[1]);
        if (isNaN(stationId)) {
            return {
                description: 'stationId can not be parsed.'
            }
        }
        return {timestamp, stationId}
    }
}