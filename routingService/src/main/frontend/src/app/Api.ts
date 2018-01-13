import {PricePredictionRequests, Route} from './DomainTypes'
import Axios from 'axios'

export class Api {
    static prefix: string = HOST + '/api';

    private static unprefixedRoutes: Routes = {
        route: '/simpleRoute',
        predictions: '/pricePredictions'
    };

    private static routes = (function(){
        const result: Routes = {};
        for (let key in Api.unprefixedRoutes) {
            result[key] = Api.prefix + Api.unprefixedRoutes[key];
        }
        return result;
    })();

    static route(route: Route) {
        return Axios.post(Api.routes['route'], route);
    }

    static predictions(request: PricePredictionRequests) {
        return Axios.post(Api.routes['predictions'], request)
    }
}

Axios.defaults.headers.post['Accept'] = 'application/json';

type Routes = {[index: string]: string}

