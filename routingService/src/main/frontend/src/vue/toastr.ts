// hipster style naming without vowels improves everything

import Vue from 'vue'
import {Component, Prop} from 'vue-typed'
import {AppError, DescribableError} from "../app/DomainTypes";

@Component({
    template: require('./toastr.html'),
})

export class Toastr extends Vue {
    @Prop()
    error?: DescribableError;
    showSnackbar: boolean = false;
    description: string = '';

    showToast() {
        if (!this.error) {
            this.showSnackbar = false;
        } else {
            this.description = this.error.describe();
            this.showSnackbar = true;
        }
    }

    mounted() {
        this.$watch('error', this.showToast)
    }
}