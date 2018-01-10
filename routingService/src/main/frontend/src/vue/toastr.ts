// hipster style naming without vowels improves everything

import Vue from 'vue'
import {Component, Prop} from 'vue-typed'
import {AppError} from "../app/DomainTypes";

@Component({
    template: require('./toastr.html'),
})

export class Toastr extends Vue {
    @Prop()
    error?: AppError;

    constructor() {
        super()
    }

    showToast() {
        const snackbar = this.$refs.snackbar;
        if (!this.error) {
            return;
        }
        // the alternative would be to create typings for mdl (no time)
        //@ts-ignore
        snackbar.MaterialSnackbar.showSnackbar({message: this.error.description});
    }

    mounted() {
        this.$watch('error', this.showToast)
    }
}