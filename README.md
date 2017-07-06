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
mvn install -pl de.logiball.monorepo.app:app-one -am
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
├── bundle│
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

To release a component like opens we are using branches. The name of the branch starts with `release/`. So a full release branch name looks like `release/service-one`.

By default all components will **not** deployed. If a component should be deployed with it's own version, you must add the following plugin configuration:

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

```
mvn clean
git branch release/<component>
git checkout release/<component>
# will improve the following delete statement in the future. Remove all unneeded directories.
git rm -r bundle/ component/service/ component/webapp/ component/lib/lib-two/ component/lib/lib-three/
# Generate pom.xml files
mr/checkout.sh
sed -i '' 's/^pom.xml$/# pom.xml/' .gitignore
find . -name pom.xml -type f  -exec chmod u+w {} \;  -exec git add {} \;
git commit -am "<component> components only"
git push --all
```
#### Release workflow

```
mvn clean
git checkout release/<component>
git merge master
git status --porcelain | grep '^DU' | cut -d' ' -f2 | xargs git rm -rf
# Regenerate pom.xml files
mr/checkout.sh
# Before running release:prepare, make 'pom.xml' writable. It's only readable by mr/checkout.sh
find . -name pom.xml -type f  -exec chmod u+w {} \;
git commit -am "merge in advance of release"

# If the git branch name is 'release/comp1' the result will be 'comp1'
componentName=$(git rev-parse --abbrev-ref HEAD | cut -f2 -d/)
mvn release:prepare --batch-mode -DautoVersionSubmodues=true -DreleaseVersion=<version> -DdevelopmentVersion=HEAD-SNAPSHOT -DtagNameFormat=$componentName-@{project.version}
mvn release:perform
```

Note that changes to the versions in the POM that the release plugin makes are not pushed back to the master branch by any process.

## Credits

The scripts in the folder `mr` are an copy of [Paul Hammants][paul-hammant-twitter] [googles-monorepo-demo][googles-monorepo-demo]. Paul also helped to work out the release workflow.

[googles-monorepo-demo]: https://github.com/paul-hammant/googles-monorepo-demo
[paul-hammant-twitter]: https://twitter.com/paul_hammant
