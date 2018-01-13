import {AppError, GasStrategy, Route, RoutePoint} from "./DomainTypes";
import * as dateformat from 'date-fns/format'
import * as dateparse from 'date-fns/parse'

export class CsvProcessor {
    private readonly name: string;

    constructor(name: string) {
        this.name = name
    }

    processRouteCsv(content: string): Route | AppError {
        const lines = content.split('\n'); //TODO check OS compat
        if (lines.length < 2) {
            return new AppError("Csv file should contain at least 2 lines (capacity and one stop).");
        }
        const capacity = parseInt(lines[0]);
        if (isNaN(capacity) || capacity < 0) {
            return new AppError("First line must be capacity in litres (positive integer).")
        }
        const stops: RoutePoint[] = [];
        for (let i = 1; i < lines.length; i++) {
            if (lines[i] === '') {
                continue
            }
            const result = CsvProcessor.parseRouteLine(lines[i]);
            if (result instanceof AppError) {
                return new AppError(`Error in line ${i + 1}: ${result.description}`)
            } else {
                stops.push(result)
            }
        }
        return new Route(capacity, stops, this.name)
    }

    static toCsv(strategy: GasStrategy): string {
        const lines: string[] = [];
        for (let stop of strategy.stops) {
            const formatted = dateformat(stop.timestamp, 'YYYY-MM-DD HH:mm:ssZZ').slice(0, -2);
            lines.push(`${formatted};${stop.station.id};${stop.price};${stop.amount}`)
        }
        return lines.join('\n')
    }

    static parseRouteLine(line: string): RoutePoint | AppError {
        const parts = line.split(';');
        if (parts.length !== 2) {
            return new AppError('should consist of two elements separated by ";".')
        }
        const date = dateparse(parts[0]);
/*        if (isNaN(date)) {
            return new AppError('timestamp can not be parsed.')
        }*/
        //const timestamp = new Date(date);
        const stationId = parseInt(parts[1]);
        if (isNaN(stationId)) {
            return new AppError('stationId can not be parsed.')
        }
        return new RoutePoint(stationId, date)
    }
}