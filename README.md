# git-usage-stats

Simple CLI tool for collecting metrics from git repository

## User guide

### Installing

Installation can be performed by:

1. Downloading archive
    * Download distribution archive from the [release page](https://github.com/FelixDes/git-usage-stats/releases)
    * Extract 
    * un script from `bin` folder for your OS
2. Building from scratch
    * Clone repository
    * Run `./gradlew cli:distZip` and take archive from `./cli/build/distributions` or install with `./gradlew cli:installDist`

### Usage

Grammar (ABNF-like):

```abnf
root = bin subcommand_with_options /
           [ -h / --help ]

bin = "./GitUsageStats"; path to startup script

subcommand_with_options = ( fmfp / gmfp ) ( link [ -c / --changes ] [ -f / --full ] ) / 
                                          [ -h / --help ]

link = https://github.com/FelixDes/git-usage-stats.git; URL or local absolute path ends with .git
```

Examples:

```shell
./GitUsageStats fmfp https://github.com/FelixDes/colsum.git -c -f
```

_Note_: default java temp dir from `java.io.tmpdir` will be used for repo loading. 
Can be overwritten with `-Djava.io.tmpdir=/var/tmp` argument.

```shell
./GitUsageStats gmfp {absolute path to .git folder}
```

## Available metrics

### FMFp

Pairs of developers who most frequently contribute to the same files/folders in a git repository.

### GMFp

Pair of developers who contribute together in the biggest number of files in a git repository.

## Contributing

If you want to improve the project, follow the steps below:

* Firstly, check the `issues` page - maybe your enchantment is already under consideration
* If there are no corresponding issues - create a new one
* Fork the repository
* Create your branch from `master`
* Create pull request to `master` branch