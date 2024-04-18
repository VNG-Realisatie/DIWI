FROM git.phinion.com/phinion/dependency_proxy/containers/node:18-alpine as build-stage

# Dependencies
COPY ./yarn.lock ./yarn.lock
COPY ./package.json ./package.json
RUN yarn

# Our code
COPY ./src ./src
COPY ./public ./public
COPY ./tsconfig.json ./tsconfig.json
COPY ./.editorconfig ./.editorconfig

ARG REACT_APP_GIT_SHA
ENV REACT_APP_GIT_SHA=${REACT_APP_GIT_SHA}
ARG REACT_APP_DEPLOY_DATE
ENV REACT_APP_DEPLOY_DATE=${REACT_APP_DEPLOY_DATE}

RUN yarn build

RUN echo "{ \
    \"git hash\": \"$REACT_APP_GIT_SHA\",\
    \"build date\": \"$REACT_APP_DEPLOY_DATE\"\
    }" > ./build/version.json

# production stage
FROM nginx:1.24-alpine as production-stage

COPY --from=build-stage ./build /usr/share/nginx/html

EXPOSE 80
