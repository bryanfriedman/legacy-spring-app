# Very Basic Legacy Spring App to Refactor

I wrote this very simple app for a blog post I wrote to demonstrate using [OpenRewrite](https://github.com/openrewrite/) for automated refactoring, and deploying an app to an edge node running [EVE-OS](https://github.com/lf-edge/eve/).

## Makefile

A Makefile offers the following targets:

- `make clean`: Run `mvn clean` and cleanup Docker environment.
- `make package`: Run `mvn package` to build the JAR file.
- `make container`: Build the Docker container and run it locally.
    - (This doesn't not handle publishing to a container registry.)
- `make rewrite`: Run the specified OpenRewrite recipes.

## Refactoring with OpenRewrite

I used OpenRewrite to refactor this code and fix three primary things:

- Use the newer dedicated `@GetMapping` as an alternative for `@RequestMapping`
- Use the SLF4J Logger instead of the basic `System.out.println`
- Upgrade from Spring Boot 2.x to 3.x

Everything can be built, refactored, and run locally using the `make` command.

## Automated Refactoring Meets Edge Deployment

Since I wrote this for a blog post that also detailed the deployment to an edge node, I've outlined those steps here as well. Once the app is built (and published to a container registry like Docker Hub), the following steps will deploy it to a local virtualized instance running EVE-OS to simulate an edge node.

### Building EVE with Eden (on MacOS)

I mostly followed [this EVE Tutorial](https://github.com/shantanoo-desai/EVE-OS-tutorials/blob/master/00-Eve-Eden-Local-QEMU.md) which was extremely helpful. However, it was written for Linux and I ran into a few snags that didn't work on MacOS which is the environment I used. As such, I ended up forking the `eden` code and tweaking a few minor things just to get everything to work. It was mostly around getting the `qemu` environment to run correctly. You can see the specifics here in the [forked repo](https://github.com/bryanfriedman/eden.git). And of course, instead of running the default `nginx` deployment to test things, I deployed this Spring app. I also found I needed to specifically configure additional port forwarding in order to reach the deployed app.

#### Prerequisites

I installed all the following prerequisites if they weren't already installed, using `brew` where possible, or otherwise downloading and installing.

- `make`
- `qemu`
- `go`
- `docker`
- `jq`
- `git`

#### Prepare and Onboard EVE

1. Start required `qemu` containers:
```
$ docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
```

2. Build Eden (I used my fork as indicated above):
```
$ git clone https://github.com/bryanfriedman/eden.git && cd eden/
$ make clean
$ make build-tests
```

3. Setup Eden configuration and prepare port 8080 for our app:
```
$ ./eden config add default
$ ./eden config set default --key eve.hostfwd --value '{"8080":"8080"}'
$ ./eden setup
```

4. Activate Eden:
```
$ tcsh
$ source ~/.eden/activate.csh
```

5. Check status, then onboard EVE:
```
$ ./eden status
$ ./eden eve onboard
$ ./eden status
```

#### Deploy the app to EVE

1. Deploy the Spring app from Docker Hub:
```
$ ./eden pod deploy --name=eve_spring docker://bryanfriedman/legacy-spring-app -p 8080:80
```

2. Wait for the pod to come up:
```
$ watch ./eden pod ps
```

3. Make sure it works:
```
$ curl http://localhost:8080
```