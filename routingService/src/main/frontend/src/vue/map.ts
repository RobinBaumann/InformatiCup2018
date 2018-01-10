import {Component} from 'vue-typed'
import Vue from 'vue'
import * as ol from 'openlayers'

@Component({
    template: require('./map.html')
})

export class Map extends Vue {
    map: ol.Map;

    mounted() {
        this.$nextTick(
            () => this.map = new ol.Map({
                target: 'map',
                layers: [
                    new ol.layer.Tile({source: new ol.source.OSM()})
                ],
                view: new ol.View({
                    center: [0, 0],
                    zoom: 4
                })
            }));
    }
}
