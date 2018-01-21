package com.github.robinbaumann.informaticup2018;

import com.github.robinbaumann.informaticup2018.model.PricePredictionRequest;
import com.github.robinbaumann.informaticup2018.model.PricePredictionRequests;
import com.github.robinbaumann.informaticup2018.model.RoutePoint;
import com.github.robinbaumann.informaticup2018.model.RouteRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvParsing {
    public static RouteRequest parseRouteCsv(String nameWithoutExt, Class clazz) throws IOException {
        List<String> lines = getLinesFromResource(nameWithoutExt, clazz);
        int capa = Integer.parseInt(lines.get(0));
        List<RoutePoint> stops = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(";");
            OffsetDateTime time = parseDate(parts[0]);
            int stationId = Integer.parseInt(parts[1]);
            stops.add(new RoutePoint(stationId, time));
        }
        return new RouteRequest(capa, stops);
    }

    public static PricePredictionRequests parsePredictionCsv(String nameWithoutExt, Class clazz) throws IOException {
        List<String> lines = getLinesFromResource(nameWithoutExt, clazz);
        List<PricePredictionRequest> requests = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(";");
            requests.add(new PricePredictionRequest(
                    parseDate(parts[0]),
                    parseDate(parts[1]),
                    Integer.parseInt(parts[2])));
        }
        return new PricePredictionRequests(requests);
    }

    private static List<String> getLinesFromResource(String nameWithoutExt, Class clazz) throws IOException {
        List<String> lines = new ArrayList<>();
        try (InputStream stream = clazz.getResourceAsStream(nameWithoutExt + ".csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                lines.add(readLine);
            }
        }
        return lines;
    }

    private static OffsetDateTime parseDate(String value) {
        //can't handle this format otherwise...
        String adjusted = value.replace(" ", "T") + ":00";
        return OffsetDateTime.parse(adjusted);
    }
}
