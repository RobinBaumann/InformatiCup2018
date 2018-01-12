import {Component, Prop} from 'vue-typed'
import Vue from 'vue'
import * as ol from 'openlayers'
import {currentPositionStyle, gasStrategyStyle, wgsToMap} from '../app/OlUtil'

@Component({
    template: require('./map.html')
})

export class Map extends Vue {
    @Prop()
    geojson?: string;
    map: ol.Map;
    currentPosition?: ol.layer.Vector = undefined
    currentStrategy?: ol.layer.Vector = undefined

    private static readonly karlsruhe: ol.Coordinate = wgsToMap([8.403653, 49.00689])

    mounted() {
        this.$nextTick(() => this.setupOl());
        this.$watch('geojson', this.strategyJsonChanged)
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
                () => this.setWgsCenter(Map.karlsruhe)
            )
        } else {
            this.setWgsCenter(Map.karlsruhe)
        }
    }

    beforeDestroy() {
        window.removeEventListener('resize', this.onResize)
    }

    private onResize() {
        this.map.updateSize();
    }

    private strategyJsonChanged() {
        if (this.geojson) {
            const source = new ol.source.Vector({
                features: (new ol.format.GeoJSON()).readFeatures(this.geojson, {
                    dataProjection: 'EPSG:4326',
                    featureProjection: 'EPSG:3857'
                })
            })
            this.currentStrategy = new ol.layer.Vector({source})
            //@ts-ignore
            this.currentStrategy.setStyle(gasStrategyStyle)
            this.map.getLayers().push(this.currentStrategy)
        }
    }

    private setWgsCenter(coordinates: ol.Coordinate) {
        this.map.getView().setCenter(wgsToMap(coordinates))
    }

    private setCurrentPosition(coordinates: ol.Coordinate) {
        if (!this.currentPosition) {
            const source = new ol.source.Vector()
            const feature = new ol.Feature({
                geometry: new ol.geom.Point(wgsToMap(coordinates)),
                name: 'aktuelle Position',

            })
            source.addFeature(feature)
            this.currentPosition = new ol.layer.Vector({source})
            //@ts-ignore seems that ol types are incomplete and broken
            this.currentPosition.setStyle(currentPositionStyle)
            this.map.getLayers().push(this.currentPosition)
        }
        this.setWgsCenter(coordinates)
    }
}

