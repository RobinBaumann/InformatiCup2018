import {
    AppError, GasStrategy, PricePredictionRequest, PricePredictionRequests, PricePredictions, Route,
    RoutePoint
} from "./DomainTypes";
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
                return new AppError(`Error in line ${i + 1}: ${result.describe()}`)
            } else {
                stops.push(result)
            }
        }
        return new Route(capacity, stops, this.name)
    }

    processPriceCsv(content: string): PricePredictionRequests | AppError {
        const lines = content.split('\n'); //TODO check os compat
        if (lines.length === 0) {
            return new AppError("Csv file should contain at least 1 line.");
        }
        const requests: PricePredictionRequest[] = [];
        for (let i = 0; i < lines.length; i++) {
            const result = CsvProcessor.parsePredictionLine(lines[i]);
            if (result instanceof AppError) {
                return new AppError(`Error in line ${i + 1}: ${result.describe()}`);
            } else {
                requests.push(result);
            }
        }
        return new PricePredictionRequests(requests, this.name);
    }

    static strategyToCsv(strategy: GasStrategy): string {
        return strategy.stops
            .map(s => `${this.formatDate(s.timestamp)};${s.station.id};${s.price};${s.amount}`)
            .join('\n');
    }

    static predictionsToCsv(predictions: PricePredictions): string {
        return predictions.predictions
            .map(p => `${this.formatDate(p.momentKnownPrices)};${this.formatDate(p.momentPrediction)};${p.station.id};${p.price}`)
            .join('\n')
    }

    private static formatDate(timestamp: Date): string {
        return dateformat(timestamp, 'YYYY-MM-DD HH:mm:ssZZ').slice(0, -2);
    }

    static parseRouteLine(line: string): RoutePoint | AppError {
        const parts = line.split(';');
        if (parts.length !== 2) {
            return new AppError('should consist of two elements separated by ";".')
        }
        // this could be problematic: dateparse is typed with Date as return value,
        // no way to check for invalid dates, this will be fixed in date-fns v2 (currently prerelease state)
        // TODO update to date-fns v2 asap and reintroduce error handling
        const date = dateparse(parts[0]);
        const stationId = parseInt(parts[1]);
        if (isNaN(stationId)) {
            return new AppError('stationId can not be parsed.')
        }
        return new RoutePoint(stationId, date)
    }

    static parsePredictionLine(line: string): PricePredictionRequest | AppError {
        const parts = line.split(';');
        if (parts.length !== 3) {
            return new AppError('should consist of three elements separated by ";".');
        }
        // this could be problematic: dateparse is typed with Date as return value,
        // no way to check for invalid dates, this will be fixed in date-fns v2 (currently prerelease state)
        // TODO update to date-fns v2 asap and reintroduce error handling
        const known = dateparse(parts[0]);
        const predict = dateparse(parts[1]);
        const stationId = parseInt(parts[2]);
        if (isNaN(stationId)) {
            return new AppError('stationId can not be parsed.');
        }
        return new PricePredictionRequest(known, predict, stationId);
    }
}