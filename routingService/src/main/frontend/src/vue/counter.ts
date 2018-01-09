import {Component} from 'vue-typed'
import Vue from 'vue'

@Component({
    template: require('./counter.html')
})

export class Counter extends Vue {
    count = 0

    increment() {
        this.count++
    }
}