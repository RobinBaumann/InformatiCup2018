import {Route} from './DomainTypes'
import Axios from 'axios'

export class Api {
    static prefix: string = HOST + '/api'

    private static unprefixedRoutes: Routes = {
        route: '/simpleRoute'
    }

    private static routes = (function(){
        const result: Routes = {}
        for (let key in Api.unprefixedRoutes) {
            result[key] = Api.prefix + Api.unprefixedRoutes[key]
        }
        return result
    })()

    static route(route: Route) {
        return Axios.post(Api.routes['route'], route)
    }
}

type Routes = {[index: string]: string}

