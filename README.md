# git-usage-stats

Simple CLI tool for collecting metrics from git repository

## User guide

#### Installing

1. Download distribution archive from the [release page](https://github.com/FelixDes/git-usage-stats/releases)
2. Extract it
3. Run script from `bin` folder for your OS

#### Usage

Grammar (aBNF-like):

```abnf
root = bin subcommand_with_options

bin = "./GitUsageStats"; path to startup script

subcommand_with_options = ( fmfp / gmfp ) link [-c] [-f]

link = https://github.com/FelixDes/git-usage-stats.git; URL ends with .git or local absolute path
```

Examples:

```shell
./GitUsageStats fmfp https://github.com/FelixDes/colsum.git -c -f
```

_Note_: default java temp dir `java.io.tmpdir` will be used for repo loading. Can be overwritten with `-Djava.io.tmpdir=/var/tmp`.

```shell
./GitUsageStats gmfp {absolute path to repos .git folder}
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