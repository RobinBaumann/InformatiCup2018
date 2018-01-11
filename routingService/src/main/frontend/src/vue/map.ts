import {Component} from 'vue-typed'
import Vue from 'vue'
import * as ol from 'openlayers'
import {wgsToMap} from '../app/OlUtil'

@Component({
    template: require('./map.html')
})

export class Map extends Vue {
    map: ol.Map;
    private static karlsruhe: ol.Coordinate = wgsToMap([8.403653, 49.00689])

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
                pos => this.setWgsCenter([pos.coords.longitude, pos.coords.latitude]),
                () => this.setWgsCenter(Map.karlsruhe)
            )
        } else {
            this.setWgsCenter(Map.karlsruhe)
        }
    }

    beforeDestroy() {
        window.removeEventListener('resize', this.onResize)
    }

    onResize() {
        this.map.updateSize();
    }

    private setWgsCenter(coordinates: ol.Coordinate) {
        this.map.getView().setCenter(wgsToMap(coordinates))
    }

}

