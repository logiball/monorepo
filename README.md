# monorepo

**Currently we are in migration to this layout and workflows.**

This repository is an extract of our internal git-maven-monorepo. Maybe it helps to create your own. We will explain more details in the nearer future.

## Use the Repository

Initial setup:

```
git clone git@github.com:logiball/monorepo.git
cd monorepo
mr/checkout.sh
```

To build **all** parts of the repository run:

```
mvn install
```

If only a specific project and it's dependencies should be build run:

```
mvn install -pl de.logiball.monorepo.service:service-one -am
```

*For detailed description of the maven parameters above run `mvn -help`.*

### Partial checkout

To configure needed parts ('app-one' in this example):

```
git config core.sparsecheckout true
echo '/mr' > .git/info/sparse-checkout
echo '/README.md' >> .git/info/sparse-checkout
echo '/pom*' >> .git/info/sparse-checkout
echo '/component/lib/*' >> .git/info/sparse-checkout
echo '/component/service/service-one/' >> .git/info/sparse-checkout
mr/checkout.sh
mvn install
```


## Directory Layout

```
monorepo/
├── bundle
├── ci
├── component
│   ├── app
│   ├── lib
│   │   ├── lib-one
│   │   ├── lib-two
│   │   └── lib-three
│   ├── service
│   │   ├── service-one
│   │   └── service-two
│   └── webapp
│       └── webapp-one
├── mr
└── README.md
```

To keep the repository organized readable pathes are essential, e.g. `/component/service/service-one` is a service and `/component/lib/lib-two` is a library. That's also one of the reasons why the singular was chosen for directory names.

Naming of directories:

* **bundle** - In a bundle, the components required to create this bundle are described in the form of scripts and configuration files.
* **ci** - Contains global configuration for the continuous integration (CI) daemon.
* **component** - A component is a closed set of sources and resources that can be used by a bundle or an other component:
    * **app** - Contains iOS, Android or other apps.
    * **lib** - Contains libraries that can be used by other components.
    * **service** - Contains components that represent a service and provide an API.
    * **webapp** - All components that represent a webapp.
* **mr** - Contains the tooling for the use of the mono repo.

## Releasing a Component

To release a component like service-on we are using branches. The name of the branch starts with `release/`. So a full release branch name looks like `release/service-one`.

By default all components will **not** deployed. If a component should be deployed with it's own version, you must add the following plugin configuration to it's `pom-template.xml`:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-deploy-plugin</artifactId>
    <configuration>
        <skip>false</skip>
    </configuration>
</plugin>
```

### To Make the Branch

This is a one time operation for every component. To create a new release branch execute the command `mr/release branch` like in the following example:

```
mr/release branch --name service-one  -- bundle/ component/service/service-two/ component/webapp/ \
        component/lib/lib-two/ component/lib/lib-three/
```

All pathes after `--` of the monorep are not needed for releasing the current component and will be deleted. In this example it is the component `service-one`.

For more details about this command execute

```
mr/release branch --help
```

### Release workflow

To make a new release of an created release branch use the command `mr/release release`. The following command must be executed on a release branch. So the name of the branch must be something like 'release/<component>'

```
mr/release release --version 0.0.2 --dry-run false
```

For more details about this command execute

```
mr/release release --help
```

## Credits

The scripts in the folder `mr` are an copy of [Paul Hammants][paul-hammant-twitter] [googles-monorepo-demo][googles-monorepo-demo]. Paul also helped to work out the release workflow.

[googles-monorepo-demo]: https://github.com/paul-hammant/googles-monorepo-demo
[paul-hammant-twitter]: https://twitter.com/paul_hammant
