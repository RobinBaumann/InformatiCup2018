# Dev

- install node
- install yarn
- cd to src/main/frontend
- `yarn install`
- `yarn run start` to start devserver on 8080

# Structure

- custom css in `frontend/src/css/app.css`
- vue components in `frontend/src/vue`

# Backend Conventions

- Errors that can be handled on user side throw a custom Exception, declared in the method signature. In the web layer
we translate these Exceptions to Problems
- Errors that can only be handled by devs can rethrow a RuntimeException.

# TODOs

- test production build with maven
- test spark serving frontend
- implement RuntimeException Handler in spark to show generic problem and Log