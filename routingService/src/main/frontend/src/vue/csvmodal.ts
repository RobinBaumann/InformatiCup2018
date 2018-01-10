import {Component} from 'vue-typed'
import Vue from 'vue'

@Component({
    template: require('./csvmodal.html')
})

export class CsvModal extends Vue {
    showModal: boolean = false;

    open() {
        this.showModal = true
    }

    close() {
        this.showModal = false
    }

    getState() {
        return this.showModal;
    }


}
