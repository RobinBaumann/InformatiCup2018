package com.github.robinbaumann.informaticup2018;

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
    public static RouteRequest parseCsv(String nameWithoutExt, Class clazz) throws IOException {
        List<String> lines = new ArrayList<>();
        try (InputStream stream = clazz.getResourceAsStream(nameWithoutExt + ".csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String readLine;
            while ((readLine = reader.readLine()) != null) {
                lines.add(readLine);
            }
        }
        int capa = Integer.parseInt(lines.get(0));
        List<RoutePoint> stops = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(";");
            //can't handle this format otherwise...
            String date = parts[0].replace(" ", "T") + ":00";
            OffsetDateTime time = OffsetDateTime.parse(date);
            int stationId = Integer.parseInt(parts[1]);
            stops.add(new RoutePoint(stationId, time));
        }
        return new RouteRequest(capa, stops);
    }
}
