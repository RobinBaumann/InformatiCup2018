import {Component, Prop} from 'vue-typed'
import Vue from 'vue'
import * as ol from 'openlayers'
import {currentPositionStyle, gasStrategyStyle, predictionsStyle, wgsToMap} from '../app/OlUtil'

@Component({
    template: require('./map.html')
})

export class Map extends Vue {
    map: ol.Map;
    currentPosition?: ol.layer.Vector = undefined;

    private readonly karlsruhe: ol.Coordinate = [8.403653, 49.00689];

    mounted() {
        this.$nextTick(() => this.setupOl());
    }

    setupOl() {
        this.map = new ol.Map({
            target: 'map',
            layers: [
                new ol.layer.Tile({source: new ol.source.OSM()})
            ],
            view: new ol.View({
                center: [0,0],
                zoom: 9
            }),
            controls: []
            });
        window.addEventListener('resize', this.onResize);
        this.onResize();
        if (window.navigator.geolocation) {
            window.navigator.geolocation.getCurrentPosition(
                pos => this.setCurrentPosition([pos.coords.longitude, pos.coords.latitude]),
                () => this.setWgsCenter(this.karlsruhe)
            )
        } else {
            this.setWgsCenter(this.karlsruhe)
        }
    }

    beforeDestroy() {
        window.removeEventListener('resize', this.onResize)
    }

    private onResize() {
        this.map.updateSize();
    }

    addRoute(geojson: string): ol.layer.Vector {
        return this.addGeoJson(geojson, gasStrategyStyle);
    }

    addPredictions(geojson: string): ol.layer.Vector {
        return this.addGeoJson(geojson, predictionsStyle);
    }

    removeLayer(layer: ol.layer.Vector) {
        this.map.removeLayer(layer);
    }

    private addGeoJson(geojson: string, stylefun: (feature: ol.Feature) => any): ol.layer.Vector {
        const source = new ol.source.Vector({
            features: (new ol.format.GeoJSON()).readFeatures(geojson, {
                dataProjection: 'EPSG:4326',
                featureProjection: 'EPSG:3857'
            })
        });
        const layer = new ol.layer.Vector({source});
        //@ts-ignore
        layer.setStyle(stylefun);
        this.map.getLayers().push(layer);
        return layer;
    }

    private setWgsCenter(coordinates: ol.Coordinate) {
        this.map.getView().setCenter(wgsToMap(coordinates))
    }

    private setCurrentPosition(coordinates: ol.Coordinate) {
        if (!this.currentPosition) {
            const source = new ol.source.Vector();
            const feature = new ol.Feature({
                geometry: new ol.geom.Point(wgsToMap(coordinates)),
                name: 'aktuelle Position',

            });
            source.addFeature(feature);
            this.currentPosition = new ol.layer.Vector({source});
            //@ts-ignore seems that ol types are incomplete and broken
            this.currentPosition.setStyle(currentPositionStyle);
            this.map.getLayers().push(this.currentPosition);
        }
        this.setWgsCenter(coordinates)
    }
}

